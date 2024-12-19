package cloud.zeen.zeenlineoaloyalty.controller.secure;

import cloud.zeen.zeenlineoaloyalty.domain.AddLineUserToZeenDBRequest;
import cloud.zeen.zeenlineoaloyalty.domain.AddLineUserToZeenDBResponse;
import cloud.zeen.zeenlineoaloyalty.domain.core.ResponseBodyTemplate;
import cloud.zeen.zeenlineoaloyalty.service.LineOAService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/secureLineOA")
public class LineOAController {

    private final LineOAService lineOAservice;

    public LineOAController(LineOAService lineOAservice) {
        this.lineOAservice = lineOAservice;
    }

    @PostMapping("/addLineUserToZeenDB")
    public ResponseEntity<ResponseBodyTemplate<AddLineUserToZeenDBResponse>> addLineUserToZeenDB(@RequestHeader("jwttoken") String token, @RequestBody AddLineUserToZeenDBRequest addLineUserToZeenDBRequest) {
        ResponseBodyTemplate<AddLineUserToZeenDBResponse> response = this.lineOAservice.addLineUserToZeenDB(token, addLineUserToZeenDBRequest); //call service
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
