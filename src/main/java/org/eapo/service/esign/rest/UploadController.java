package org.eapo.service.esign.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class UploadController {

    @RequestMapping(value="/uploadFile", method= RequestMethod.POST)
public String uploadFile(@RequestParam("user-file") MultipartFile file) throws IOException {


      //  System.out.println(name);
       System.out.println(file.getBytes().length);

    return "ASDF";
}

}
