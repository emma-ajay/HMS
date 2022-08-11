package com.project.Hms.Controller;
import com.project.Hms.DTO.Requests.CreateHall;
import com.project.Hms.DTO.Requests.CreateWing;
import com.project.Hms.DTO.Response.GenericResponse;
import com.project.Hms.Entity.Hall;
import com.project.Hms.Entity.Wing;
import com.project.Hms.Service.HallService;
import com.project.Hms.Service.WingService;
import com.project.Hms.mapper.HallMapper;
import com.project.Hms.mapper.WingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/halls")
public class HallController {

    @Autowired
    private HallService hallService;

    @Autowired
    private HallMapper hallMapper;

    @Autowired
    private WingMapper wingMapper;


    @Autowired
    private WingService wingService;


    // view all halls
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_PORTER', 'ROLE_STUDENT')")
    @GetMapping
    public ResponseEntity<GenericResponse> viewHalls() {
        try {
            String message = "Request successful";
            if (hallService.getHalls() == null) {
                message = "No Halls Available";
            }

            return new ResponseEntity<>
                    (new GenericResponse("00",
                            HttpStatus.OK,
                            message,
                            hallService.getHalls()),
                            new HttpHeaders(),
                            HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new GenericResponse("99",
                    HttpStatus.BAD_REQUEST,
                    e.getLocalizedMessage()),
                    new HttpHeaders(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    //view a hall by id
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_PORTER', 'ROLE_STUDENT')")
    @GetMapping(path = "/view/{hallId}")
    public ResponseEntity<GenericResponse> viewHallById(@PathVariable("hallId") Long hallId) {
        try {
            String message = "Request successful";
            Hall hall = hallService.findHallById(hallId);
            if (hall == null) {
                message = "Hall does not exist";
                return new ResponseEntity<>(new GenericResponse(
                        "99", HttpStatus.BAD_REQUEST,
                        message),
                        new HttpHeaders(),
                        HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>
                    (new GenericResponse("00",
                            HttpStatus.OK,
                            message,
                            hallService.findHallById(hallId)),
                            new HttpHeaders(),
                            HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new GenericResponse("99",
                    HttpStatus.BAD_REQUEST,
                    e.getLocalizedMessage()),
                    new HttpHeaders(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    //view a hall by name
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_PORTER', 'ROLE_STUDENT')")
    @GetMapping(path = "{hallName}/view")
    public ResponseEntity<GenericResponse> viewHallByName(@PathVariable  String hallName) {
        try {
            String message = "Request successful";
            Hall hall = hallService.findHallByName(hallName);
            if (hall == null) {
                message = "Hall does not exist";
                return new ResponseEntity<>(new GenericResponse(
                        "99", HttpStatus.BAD_REQUEST,
                        message),
                        new HttpHeaders(),
                        HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>
                    (new GenericResponse("00",
                            HttpStatus.OK,
                            message,
                            hallService.findHallByName(hallName)),
                            new HttpHeaders(),
                            HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new GenericResponse("99",
                    HttpStatus.BAD_REQUEST,
                    e.getLocalizedMessage()),
                    new HttpHeaders(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    //view a hall by gender
    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_PORTER', 'ROLE_STUDENT')")
    @GetMapping(path = "{hallGender}/view")
    public ResponseEntity<GenericResponse> viewHallByGender(@RequestParam(value="hallGender") String hallGender) {
        try {
            String message = "Request successful";
            List<Hall> gender_halls = hallService.findHallsByGender(hallGender);
            if (gender_halls == null) {
                message = "No halls available"; //should there be bad request
            }
            return new ResponseEntity<>
                    (new GenericResponse("00",
                            HttpStatus.OK,
                            message,
                            hallService.findHallsByGender(hallGender)),
                            new HttpHeaders(),
                            HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new GenericResponse("99",
                    HttpStatus.BAD_REQUEST,
                    e.getLocalizedMessage()),
                    new HttpHeaders(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    //create a hall
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/create_hall")
    public ResponseEntity<GenericResponse> createAHall(
            @RequestBody CreateHall CreateHall) {
        try {

            Hall hall = hallService.findHallByName(CreateHall.getHallName()); //what of casing?

            if (hall != null) {

                return new ResponseEntity<>(new GenericResponse(
                        "99", HttpStatus.BAD_REQUEST,
                        "Hall Already Exists"),
                        new HttpHeaders(),
                        HttpStatus.BAD_REQUEST);
            }
//
//            LocalDateTime localDateTime = LocalDateTime.now();
//            String dateTime = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            hall = hallMapper.toHall(CreateHall);
            hallService.save(hall);

            return new ResponseEntity<>(new GenericResponse(
                    "00", HttpStatus.CREATED,
                    "Hall Created Successfully", hall),
                    new HttpHeaders(),
                    HttpStatus.CREATED);

        } catch (Exception e) {

            e.printStackTrace();
            return new ResponseEntity<>(new GenericResponse(
                    "99",
                    HttpStatus.BAD_REQUEST,
                    e.getLocalizedMessage()),
                    new HttpHeaders(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    //update a hall
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/update_Hall/{hallId}")
    public ResponseEntity<GenericResponse> updateHall(@PathVariable("hallId") Long hallId,
                                                      @RequestBody CreateHall updateCreateHall) {// request params instead

        try {
            String message = "Hall updated successfully";
            Hall hall = hallService.findHallById(hallId);

            if (hall == null) {
                message = "Hall does not exist";
                return new ResponseEntity<>(new GenericResponse(
                        "99", HttpStatus.BAD_REQUEST,
                        message),
                        new HttpHeaders(),
                        HttpStatus.BAD_REQUEST);
            }

            //last modified?
            return new ResponseEntity<>
                    (new GenericResponse("00",
                            HttpStatus.OK,
                            message,
                            hallService.updateHall(hall, updateCreateHall)),
                            new HttpHeaders(),
                            HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new GenericResponse("99",
                    HttpStatus.BAD_REQUEST,
                    e.getLocalizedMessage()),
                    new HttpHeaders(),
                    HttpStatus.BAD_REQUEST);
        }
    }

//    @PostMapping("add_wing/{hallId}")//change
//    public ResponseEntity<GenericResponse> addWingToHall(@PathVariable("hallId") Long hallId,
//                                                         @RequestBody WingDTO wingDTO) {
//
//        try {
//            String message = "Wing successfully added";
//            Hall hall = hallService.findHallById(hallId);
//
//            if (hall == null) {
//                message = "Hall does not exist";
//                return new ResponseEntity<>(new GenericResponse(
//                        "99", HttpStatus.BAD_REQUEST,
//                        message),
//                        new HttpHeaders(),
//                        HttpStatus.BAD_REQUEST);
//            }
//            Wing wing = new Wing(wingDTO.getWingName(), hall);
//            wingService.save(wing);//necessary?
//
//            hall.getWings().add(wing);
//            //restrict wing creation?
//            return new ResponseEntity<>
//                    (new GenericResponse("00",
//                            HttpStatus.OK,
//                            message,
//                            wing),
//                            new HttpHeaders(),
//                            HttpStatus.CREATED);
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new ResponseEntity<>(new GenericResponse("99",
//                    HttpStatus.BAD_REQUEST,
//                    e.getLocalizedMessage()),
//                    new HttpHeaders(),
//                    HttpStatus.BAD_REQUEST);
//        }
//
//        }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/create_wing")
    public ResponseEntity<GenericResponse> createAWing(
            @RequestBody CreateWing createWing) {
        try {

            Wing wing = wingService.findWingByHall(createWing.getWingName()); //what of casing?

            if (wing != null) {

                return new ResponseEntity<>(new GenericResponse(
                        "99", HttpStatus.BAD_REQUEST,
                        "Wing Already Exists"),
                        new HttpHeaders(),
                        HttpStatus.BAD_REQUEST);
            }
//
//            LocalDateTime localDateTime = LocalDateTime.now();
//            String dateTime = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            wing = wingMapper.toWing(createWing);
            wingService.save(wing);

            return new ResponseEntity<>(new GenericResponse(
                    "00", HttpStatus.CREATED,
                    "Wing Created Successfully", wing),
                    new HttpHeaders(),
                    HttpStatus.CREATED);

        } catch (Exception e) {

            e.printStackTrace();
            return new ResponseEntity<>(new GenericResponse(
                    "99",
                    HttpStatus.BAD_REQUEST,
                    e.getLocalizedMessage()),
                    new HttpHeaders(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_PORTER', 'ROLE_STUDENT')")
    @GetMapping(path = "/viewWings")
    public ResponseEntity<GenericResponse> viewWings() {
        try {
            String message = "Request successful";
            if (wingService.getAllWings() == null) {
                message = "No Wings Available";
            }

            return new ResponseEntity<>
                    (new GenericResponse("00",
                            HttpStatus.OK,
                            message,
                            wingService.getAllWings()),
                            new HttpHeaders(),
                            HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new GenericResponse("99",
                    HttpStatus.BAD_REQUEST,
                    e.getLocalizedMessage()),
                    new HttpHeaders(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/update_wing/{wingId}")
    public ResponseEntity<GenericResponse> updateWing(@PathVariable("wingId") Long wingId,
                                                      @RequestBody CreateWing updateWing) {// request params instead

        try {
            String message = "Wing updated successfully";
            Wing wing = wingService.getById(wingId);

            if (wing == null) {
                message = "Wing does not exist";
                return new ResponseEntity<>(new GenericResponse(
                        "99", HttpStatus.BAD_REQUEST,
                        message),
                        new HttpHeaders(),
                        HttpStatus.BAD_REQUEST);
            }

            //last modified?
            return new ResponseEntity<>
                    (new GenericResponse("00",
                            HttpStatus.OK,
                            message,
                            wingService.updateWing(wing, updateWing)),
                            new HttpHeaders(),
                            HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(new GenericResponse("99",
                    HttpStatus.BAD_REQUEST,
                    e.getLocalizedMessage()),
                    new HttpHeaders(),
                    HttpStatus.BAD_REQUEST);
        }

    }

}
