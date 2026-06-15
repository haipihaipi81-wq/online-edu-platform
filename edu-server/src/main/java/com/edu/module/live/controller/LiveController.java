package com.edu.module.live.controller;

import com.edu.common.PageResult;
import com.edu.common.Result;
import com.edu.module.live.entity.LiveRoom;
import com.edu.module.live.service.LiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/live")
@RequiredArgsConstructor
public class LiveController {

    private final LiveService liveService;

    @GetMapping("/rooms")
    public Result<?> listRooms(@RequestParam(defaultValue = "1") Integer page,
                               @RequestParam(defaultValue = "10") Integer size) {
        return Result.ok(PageResult.from(liveService.listRooms(page, size)));
    }

    @GetMapping("/rooms/{id}")
    public Result<?> getRoom(@PathVariable Long id) {
        return Result.ok(liveService.getRoomById(id));
    }

    @PostMapping("/rooms")
    public Result<?> createRoom(Authentication auth, @RequestBody LiveRoom room) {
        room.setTeacherId((Long) auth.getCredentials());
        return Result.ok(liveService.createRoom(room));
    }

    @PostMapping("/rooms/{id}/start")
    public Result<?> startLive(@PathVariable Long id) {
        liveService.startLive(id);
        return Result.ok();
    }

    @PostMapping("/rooms/{id}/end")
    public Result<?> endLive(@PathVariable Long id) {
        liveService.endLive(id);
        return Result.ok();
    }

    @GetMapping("/rooms/{id}/chats")
    public Result<?> getChatHistory(@PathVariable Long id,
                                    @RequestParam(defaultValue = "50") int limit) {
        return Result.ok(liveService.getChatHistory(id, limit));
    }

    @GetMapping("/rooms/{id}/records")
    public Result<?> getRecords(@PathVariable Long id) {
        return Result.ok(liveService.getRecords(id));
    }
}
