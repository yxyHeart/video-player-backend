package com.yxy.videoplayerbackend.service.impl;

import com.yxy.videoplayerbackend.common.CommonResult;
import com.yxy.videoplayerbackend.dto.LoginDto;
import com.yxy.videoplayerbackend.dto.LoginResponseDto;
import com.yxy.videoplayerbackend.dto.RegisterDto;
import com.yxy.videoplayerbackend.entity.UsersWorks;
import com.yxy.videoplayerbackend.mapper.UserMapper;
import com.yxy.videoplayerbackend.mapper.UsersWorksMapper;
import com.yxy.videoplayerbackend.service.UserService;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import com.yxy.videoplayerbackend.entity.User;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Service("userService")
public class UserServiceImpl implements UserService {
    @Autowired
    private  UserMapper userMapper;
    @Autowired
    private UsersWorksMapper usersWorksMapper;


    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;



    @Override
    public CommonResult<LoginResponseDto> login(LoginDto loginDTO) {
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();
        User user = userMapper.findByUsername(username);
        if (user == null || !BCrypt.checkpw(password, user.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        // 生成JWT
        String token = this.generateToken(user);
        return CommonResult.success(new LoginResponseDto(user.getUsername(),user.getAvatar(),token));
    }

    @Override
    public void register(RegisterDto registerDto) {
        String username = registerDto.getUsername();
        String password = registerDto.getPassword();
        User existingUser = userMapper.findByUsername(username);
        if (existingUser != null) {
            throw new RuntimeException("Username already exists");
        }
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        User user = new User();
        user.setUsername(username);
        user.setPassword(hashedPassword);

        userMapper.insert(user);
    }

    public void validateToken(String token) {
        try {
            Claims jwtClaims = Jwts.parser()
                    .setSigningKey(jwtSecret)
                    .parseClaimsJws(token)
                    .getBody();

            // 检查令牌是否过期
            Date tokenExpiration = jwtClaims.getExpiration();
            if (tokenExpiration.before(new Date())) {
                throw new RuntimeException("Token has expired");
            }

            // 其他验证逻辑...
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Token has expired");
        } catch (MalformedJwtException | SignatureException e) {
            throw new RuntimeException("Invalid token");
        }
    }

    public String generateToken(User user) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("username", user.getUsername());
        Date now = new Date();
        Date expiration = new Date(now.getTime() + jwtExpiration * 1000);
        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();

        return token;
    }
    public static final String UPLOAD_PATH = "/Users/yxy/Desktop/video-player-upload";
    public CommonResult<Map<String, String>> uploadBig(Long chunkSize, Integer totalNumber, Long chunkNumber, String md5, MultipartFile file) throws IOException{
        //文件存放位置
        String dstFile = String.format("%s/%s/%s.%s", UPLOAD_PATH, md5,md5, StringUtils.getFilenameExtension(file.getOriginalFilename()));
        //上传分片信息存放位置
        String confFile = String.format("%s/%s/%s.conf", UPLOAD_PATH, md5, md5);
        //第一次创建分片记录文件
        //创建目录
        File dir = new File(dstFile).getParentFile();
        System.out.println(dir);
        if (!dir.exists()) {
            dir.mkdir();
            //所有分片状态设置为0
            byte[] bytes = new byte[totalNumber];
            Files.write(Path.of(confFile), bytes);
        }
        //随机分片写入文件
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(dstFile, "rw");
             RandomAccessFile randomAccessConfFile = new RandomAccessFile(confFile, "rw");
             InputStream inputStream = file.getInputStream()) {
            //定位到该分片的偏移量
            randomAccessFile.seek(chunkNumber * chunkSize);
            //写入该分片数据
            randomAccessFile.write(inputStream.readAllBytes());
            //定位到当前分片状态位置
            randomAccessConfFile.seek(chunkNumber);
            //设置当前分片上传状态为1
            randomAccessConfFile.write(1);
        }
        return CommonResult.success(Map.of("path", dstFile));


    }
    public CommonResult<Map<String, String>> checkFile(String userId, String md5) throws Exception{
        String uploadPath = String.format("%s/%s/%s.conf", UPLOAD_PATH, md5, md5);
        Path path = Path.of(uploadPath);
        //MD5目录不存在文件从未上传过
        if (!Files.exists(path.getParent())) {
            return CommonResult.failed("文件未上传");
        }
        //判断文件是否上传成功
        StringBuilder stringBuilder = new StringBuilder();
        byte[] bytes = Files.readAllBytes(path);

        for (byte b : bytes) {
            stringBuilder.append(String.valueOf(b));
        }
        System.out.println(stringBuilder.toString());
        //所有分片上传完成计算文件MD5
        if (!stringBuilder.toString().contains("0")) {
            File file = new File(String.format("%s/%s/", UPLOAD_PATH, md5));
            File[] files = file.listFiles();
            String filePath = "";
            for (File f : files) {
                //计算文件MD5是否相等
                if (!f.getName().contains("conf")) {
                    filePath = f.getAbsolutePath();
                    try (InputStream inputStream = new FileInputStream(f)) {
                        String md5pwd = DigestUtils.md5DigestAsHex(inputStream);
                        if (!md5pwd.equalsIgnoreCase(md5)) {
                            return CommonResult.failed("文件上传失败");
                        }
                    }
                }
            }
            UsersWorks usersWorks = new UsersWorks(userId,filePath);
            usersWorksMapper.insert(usersWorks);
            return CommonResult.success(Map.of("path", filePath));
        } else {
            //文件未上传完成，反回每个分片状态，前端将未上传的分片继续上传
            return CommonResult.success(Map.of("chucks", stringBuilder.toString()));
        }
    }
}