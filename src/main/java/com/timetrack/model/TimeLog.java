package com.timetrack.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * Model representing a Time Log entry.
 */
public class TimeLog implements Serializable, Comparable<TimeLog> {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private Integer userId;
    private String userName; // Convenient field for display in admin/reports
    private Date logDate;
    private Time startTime;
    private Time endTime;
    private String task;
    private BigDecimal duration; // in hours, e.g. 2.50
    private Timestamp createdAt;

    public TimeLog() {
    }

    public TimeLog(Integer userId, Date logDate, Time startTime, Time endTime, String task, BigDecimal duration) {
        this.userId = userId;
        this.logDate = logDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.task = task;
        this.duration = duration;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getLogDate() {
        return logDate;
    }

    public void setLogDate(Date logDate) {
        this.logDate = logDate;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public BigDecimal getDuration() {
        return duration != null ? duration.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
    }

    public void setDuration(BigDecimal duration) {
        this.duration = duration;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Compare by logDate descending, then startTime descending
     */
    @Override
    public int compareTo(TimeLog other) {
        if (other == null) return -1;
        int dateCompare = other.getLogDate().compareTo(this.logDate);
        if (dateCompare != 0) {
            return dateCompare;
        }
        if (other.getStartTime() != null && this.startTime != null) {
            return other.getStartTime().compareTo(this.startTime);
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeLog timeLog = (TimeLog) o;
        return Objects.equals(id, timeLog.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "TimeLog{" +
                "id=" + id +
                ", userId=" + userId +
                ", logDate=" + logDate +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", task='" + task + '\'' +
                ", duration=" + duration +
                '}';
    }
}
