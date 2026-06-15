package com.edu.module.live.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.edu.common.BusinessException;
import com.edu.module.live.entity.LiveChat;
import com.edu.module.live.entity.LiveRecord;
import com.edu.module.live.entity.LiveRoom;
import com.edu.module.live.mapper.LiveChatMapper;
import com.edu.module.live.mapper.LiveRecordMapper;
import com.edu.module.live.mapper.LiveRoomMapper;
import com.edu.module.user.entity.User;
import com.edu.module.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LiveService {

    private final LiveRoomMapper liveRoomMapper;
    private final LiveChatMapper liveChatMapper;
    private final LiveRecordMapper liveRecordMapper;
    private final UserMapper userMapper;

    public Page<LiveRoom> listRooms(Integer page, Integer size) {
        return liveRoomMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<LiveRoom>().orderByDesc(LiveRoom::getCreatedAt));
    }

    public LiveRoom getRoomById(Long id) {
        LiveRoom room = liveRoomMapper.selectById(id);
        if (room == null) {
            throw new BusinessException(404, "直播间不存在");
        }
        return room;
    }

    public LiveRoom createRoom(LiveRoom room) {
        room.setStatus("WAITING");
        liveRoomMapper.insert(room);
        return room;
    }

    public void updateRoomStatus(Long id, String status) {
        LiveRoom room = new LiveRoom();
        room.setId(id);
        room.setStatus(status);
        liveRoomMapper.updateById(room);
    }

    public void startLive(Long id) {
        LiveRoom room = liveRoomMapper.selectById(id);
        room.setStatus("LIVING");
        room.setStartTime(LocalDateTime.now());
        liveRoomMapper.updateById(room);
    }

    public void endLive(Long id) {
        LiveRoom room = liveRoomMapper.selectById(id);
        room.setStatus("ENDED");
        room.setEndTime(LocalDateTime.now());
        liveRoomMapper.updateById(room);
    }

    // Chat
    public LiveChat saveChat(Long roomId, Long userId, String content) {
        LiveChat chat = new LiveChat();
        chat.setRoomId(roomId);
        chat.setUserId(userId);
        chat.setContent(content);
        liveChatMapper.insert(chat);
        User user = userMapper.selectById(userId);
        if (user != null) {
            chat.setUsername(user.getUsername());
            chat.setAvatar(user.getAvatar());
        }
        return chat;
    }

    public List<LiveChat> getChatHistory(Long roomId, int limit) {
        Page<LiveChat> page = liveChatMapper.selectPage(
                new Page<>(1, limit),
                new LambdaQueryWrapper<LiveChat>().eq(LiveChat::getRoomId, roomId).orderByDesc(LiveChat::getCreatedAt));
        List<LiveChat> list = page.getRecords();
        // Load usernames
        for (LiveChat chat : list) {
            User user = userMapper.selectById(chat.getUserId());
            if (user != null) {
                chat.setUsername(user.getUsername());
                chat.setAvatar(user.getAvatar());
            }
        }
        return list;
    }

    // Record
    public void saveRecord(Long roomId, String recordUrl, Integer duration, Long fileSize) {
        LiveRecord record = new LiveRecord();
        record.setRoomId(roomId);
        record.setRecordUrl(recordUrl);
        record.setDuration(duration);
        record.setFileSize(fileSize);
        liveRecordMapper.insert(record);
    }

    public List<LiveRecord> getRecords(Long roomId) {
        return liveRecordMapper.selectList(
                new LambdaQueryWrapper<LiveRecord>().eq(LiveRecord::getRoomId, roomId));
    }
}
