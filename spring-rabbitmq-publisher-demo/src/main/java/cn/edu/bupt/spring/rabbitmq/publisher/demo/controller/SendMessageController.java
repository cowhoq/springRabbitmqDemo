package cn.edu.bupt.spring.rabbitmq.publisher.demo.controller;

import cn.edu.bupt.spring.rabbitmq.publisher.demo.model.RideRequest;
import cn.edu.bupt.spring.rabbitmq.publisher.demo.repository.RideRequestRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "发送消息")
@RestController
@RequestMapping("/api/v1")
public class SendMessageController {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    RideRequestRepository rideRequestRepository;

    @Operation(summary = "发布打车请求")
    @GetMapping("/sendRideRequest")
    public String sendRideRequest(
            @RequestParam String passengerName,
            @RequestParam String pickupLocation,
            @RequestParam String dropoffLocation,
            @RequestParam String contactNumber,
            @RequestParam(required = false) String additionalNotes) {
        System.out.println("Received parameters:");
        System.out.println("passengerName: " + passengerName);
        System.out.println("pickupLocation: " + pickupLocation);
        System.out.println("dropoffLocation: " + dropoffLocation);
        System.out.println("contactNumber: " + contactNumber);
        System.out.println("additionalNotes: " + additionalNotes);

        String messageId = UUID.randomUUID().toString();
        LocalDateTime createTime = LocalDateTime.now();
        RideRequest rideRequest = new RideRequest();

        rideRequest.setMessageId(messageId);
        rideRequest.setPassengerName(passengerName);
        rideRequest.setPickupLocation(pickupLocation);
        rideRequest.setDropoffLocation(dropoffLocation);
        rideRequest.setContactNumber(contactNumber);
        rideRequest.setAdditionalNotes(additionalNotes);
        rideRequest.setCreateTime(createTime);
        rideRequest.setAccepted(false);

        rideRequestRepository.save(rideRequest);

        Map<String, Object> map = new HashMap<>();
        map.put("messageId", messageId);
        map.put("passengerName", passengerName);
        map.put("pickupLocation", pickupLocation);
        map.put("dropoffLocation", dropoffLocation);
        map.put("contactNumber", contactNumber);
        if (additionalNotes != null) {
            map.put("additionalNotes", additionalNotes);
        }
        map.put("createTime", createTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        rabbitTemplate.convertAndSend("rideRequestExchange", "rideRequestRouting", map);
        return messageId;
    }

    @Operation(summary = "查询打车请求")
    @GetMapping("/queryRideRequest")
    public String queryRideRequest(@RequestParam String messageId) {
        Optional<RideRequest> rideRequest = rideRequestRepository.findByMessageId(messageId);
        if (rideRequest.isPresent()) {
            return rideRequest.get().isAccepted() ? "已接单" : "未接单";
        } else {
            return "请求不存在";
        }
    }
}
