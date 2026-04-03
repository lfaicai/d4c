package org.faicai.d4c.controller;

import lombok.RequiredArgsConstructor;
import org.faicai.d4c.pojo.entity.UserHistory;
import org.faicai.d4c.service.UserHistoryService;
import org.faicai.d4c.utils.R;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/history")
public class UserHistoryController {

    private final UserHistoryService userHistoryService;

    @GetMapping("findCurrentHistory")
    public R<List<UserHistory>> findCurrentHistory() {
        return R.ok(userHistoryService.findCurrentHistory());
    }

    @PutMapping("saveOrUpdate")
    public R<UserHistory> saveOrUpdate(@RequestBody UserHistory userHistory) {
        return userHistoryService.saveOrUpdate(userHistory) ? R.ok(userHistory) : R.failed();
    }

    @DeleteMapping("remove/{historyId}")
    public R<UserHistory> remove(@PathVariable String historyId) {
        return userHistoryService.removeById(historyId) ? R.ok() : R.failed();
    }

}
