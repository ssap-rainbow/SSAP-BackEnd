package ssap.ssap.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Controller
@Tag(name = "Test 컨트롤러", description = "Test API입니다.")
public class TestController {
    @RequestMapping(value = "/api/test", method = RequestMethod.GET)
    @ResponseBody
    public String test() {
        return "test";
    }
}
