package com.increff.pos.controller.file;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;

import com.increff.pos.service.exception.ApiException;
import lombok.extern.log4j.Log4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.increff.pos.util.IOUtil;

@Log4j
@Controller
public class SampleController {

    // Spring ignores . (dot) in the path. So we need fileName:.+
    // See https://stackoverflow.com/questions/16332092/spring-mvc-pathvariable-with-dot-is-getting-truncated
    @RequestMapping(value = "/sample/{fileName:.+}", method = RequestMethod.GET)
    public void getFile(@PathVariable("fileName") String fileName, HttpServletResponse response) throws ApiException {
        // get your file as InputStream
        response.setContentType("text/csv");
        response.addHeader("Content-disposition:", "attachment; filename=" + fileName);
        String fileClasspath = "/com/increff/pos/" + fileName;
        InputStream is = SampleController.class.getResourceAsStream(fileClasspath);

        // copy it to response's OutputStream
        try {
            assert is != null;
            IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new ApiException("There was internal server error");
        } finally {
            IOUtil.closeQuietly(is);
        }

    }

}