package demo.demo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class hello {
    @GetMapping("/hello")
    public String sayhello(){
        return "Hello world from springboot, Crt Day 3";
    }
}
