package com.timetrack.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Data Transfer Object containing calculated productivity metrics and summaries.
 */
public class ProductivitySummary implements Serializable {
    private static final long serialVersionUID = 1L;

    private BigDecimal totalHours = BigDecimal.ZERO;
    private int totalEntries = 0;
    private BigDecimal averageHoursPerDay = BigDecimal.ZERO;
    private int uniqueActiveDays = 0;
    private String topTask = "N/A";
    private BigDecimal topTaskHours = BigDecimal.ZERO;

    // Daily breakdown: Date string -> Hours
    private Map<String, BigDecimal> dailyHours = new LinkedHashMap<>();

    // Task breakdown: Task name -> Hours
    private Map<String, BigDecimal> taskHours = new LinkedHashMap<>();

    // User breakdown: User name -> Hours (primarily for admin reports)
    private Map<String, BigDecimal> userHours = new LinkedHashMap<>();

    public ProductivitySummary() {
    }

    public BigDecimal getTotalHours() {
        return totalHours.setScale(2, RoundingMode.HALF_UP);
    }

    public void setTotalHours(BigDecimal totalHours) {
        this.totalHours = totalHours;
    }

    public int getTotalEntries() {
        return totalEntries;
    }

    public void setTotalEntries(int totalEntries) {
        this.totalEntries = totalEntries;
    }

    public BigDecimal getAverageHoursPerDay() {
        return averageHoursPerDay.setScale(2, RoundingMode.HALF_UP);
    }

    public void setAverageHoursPerDay(BigDecimal averageHoursPerDay) {
        this.averageHoursPerDay = averageHoursPerDay;
    }

    public int getUniqueActiveDays() {
        return uniqueActiveDays;
    }

    public void setUniqueActiveDays(int uniqueActiveDays) {
        this.uniqueActiveDays = uniqueActiveDays;
    }

    public String getTopTask() {
        return topTask;
    }

    public void setTopTask(String topTask) {
        this.topTask = topTask;
    }

    public BigDecimal getTopTaskHours() {
        return topTaskHours.setScale(2, RoundingMode.HALF_UP);
    }

    public void setTopTaskHours(BigDecimal topTaskHours) {
        this.topTaskHours = topTaskHours;
    }

    public Map<String, BigDecimal> getDailyHours() {
        return dailyHours;
    }

    public void setDailyHours(Map<String, BigDecimal> dailyHours) {
        this.dailyHours = dailyHours;
    }

    public Map<String, BigDecimal> getTaskHours() {
        return taskHours;
    }

    public void setTaskHours(Map<String, BigDecimal> taskHours) {
        this.taskHours = taskHours;
    }

    public Map<String, BigDecimal> getUserHours() {
        return userHours;
    }

    public void setUserHours(Map<String, BigDecimal> userHours) {
        this.userHours = userHours;
    }
}
