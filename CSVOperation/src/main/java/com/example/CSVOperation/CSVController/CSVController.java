package com.example.CSVOperation.CSVController;

import com.example.CSVOperation.CSVEntity.CSVModel;
import com.example.CSVOperation.CSVRepository.CSVService;
import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class CSVController {
    @Autowired
    CSVService service;
    @PostMapping("/upload")
    public ResponseEntity<String> uploadData(@RequestParam("files") MultipartFile[] files) throws Exception {
        List<CSVModel> userdetails = new ArrayList<>();
        for (MultipartFile file : files) {
            String ext = FilenameUtils.getExtension(file.getOriginalFilename());
            if (ext == "") {
                try {
                    return new ResponseEntity<>("Please attach csv file...", HttpStatus.NOT_ACCEPTABLE);
                }
            }
            else {
                try {
                    InputStream inputStream = file.getInputStream();
                    CsvParserSettings setting = new CsvParserSettings();
                    setting.setHeaderExtractionEnabled(true);
                    CsvParser parser = new CsvParser(setting);
                    List<Record> parseAllRecords = parser.parseAllRecords(inputStream);
                    parseAllRecords.forEach(record -> {
                        CSVModel user = new CSVModel();
                        user.setName(record.getString("name"));
                        user.setAddress(record.getString("address"));
                        user.setEmail(record.getString("email"));
                        userdetails.add(user);
                    });
                    service.saveAll(userdetails);
                }
                catch(Exception e)
                {
                    return new ResponseEntity<>(ext+" files not supported...",HttpStatus.UNSUPPORTED_MEDIA_TYPE);
                }
            }


        }
        return new ResponseEntity<>("Upload Successful...", HttpStatus.OK);
    }
    @GetMapping("/employees")
    public List<CSVModel> method()
    {
      return service.findAll();
    }
    @GetMapping("/employees/{id}")
    public CSVModel getUserById(@PathVariable int id)
    {
        return service.findById(id).orElse(null);
    }
    @DeleteMapping("/user/{id}")
    public String deleteUser(@PathVariable int id)
    {
        service.deleteById(id);
        return "Employee user got deleted";
    }
    @PutMapping("/updateemployee")
    public CSVModel update(@RequestBody CSVModel model)
    {
        saveOrUpdate(model);
        return model;
    }
    public void saveOrUpdate(CSVModel model)
    {
        service.save(model);
    }

}
