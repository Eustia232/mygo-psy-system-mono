package com.mygo.controller.admin;

import com.mygo.domain.dto.RegisterDTO;
import com.mygo.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
//@RequestMapping("/test")
@Tag(name = "测试接口")
public class HelloController {

    private final static Map<String, AtomicInteger> countmap = new HashMap<>();

    @Operation(summary = "测试接口")
    @GetMapping("/hello")
    public Result<String> hello(HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        AtomicInteger count = countmap.computeIfAbsent(ip, k -> new AtomicInteger(0));
        return Result.success(String.format("Hello world %d", count.incrementAndGet()));
    }

    @PostMapping("/complete")
    public Result<Void> test(@RequestBody @Valid RegisterDTO registerDTO) {
        return Result.success();
    }

//    @GetMapping("/fxe.png")
//    public ResponseEntity<Resource> image() {
//        Resource resource = new ClassPathResource("PEG.png");
//
//        // 返回图片并设置响应头
//        return ResponseEntity.ok()
//                .contentType(MediaType.IMAGE_JPEG)
////                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"your-image.jpg\"")
//                .body(resource);
//    }
}
