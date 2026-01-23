package uk.gov.legislation.endpoints.effects;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import uk.gov.legislation.data.marklogic.changes.Parameters;

public class EffectsParameters {

    private String targetType;
    private Integer targetYear;
    private Integer targetNumber;
    private Integer targetStartYear;
    private Integer targetEndYear;
    private String targetTitle;
    private String sourceType;
    private Integer sourceYear;
    private Integer sourceNumber;
    private Integer sourceStartYear;
    private Integer sourceEndYear;
    private String sourceTitle;
    private Parameters.AppliedStatus applied;
    private Parameters.EffectsSort sort;
    private Parameters.OrderBy orderBy;
    private Integer page;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Parameters.AppliedStatus getApplied() {
        return applied;
    }

    public void setApplied(Parameters.AppliedStatus applied) {
        this.applied = applied;
    }

    public Parameters.EffectsSort getSort() {
        return sort;
    }

    public void setSort(Parameters.EffectsSort sort) {
        this.sort = sort;
    }

    public Parameters.OrderBy getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(Parameters.OrderBy orderBy) {
        this.orderBy = orderBy;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(defaultValue = "1")
    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Integer getSourceEndYear() {
        return sourceEndYear;
    }

    public void setSourceEndYear(Integer sourceEndYear) {
        this.sourceEndYear = sourceEndYear;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Integer getSourceNumber() {
        return sourceNumber;
    }

    public void setSourceNumber(Integer sourceNumber) {
        this.sourceNumber = sourceNumber;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Integer getSourceStartYear() {
        return sourceStartYear;
    }

    public void setSourceStartYear(Integer sourceStartYear) {
        this.sourceStartYear = sourceStartYear;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getSourceTitle() {
        return sourceTitle;
    }

    public void setSourceTitle(String sourceTitle) {
        this.sourceTitle = sourceTitle;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Integer getSourceYear() {
        return sourceYear;
    }

    public void setSourceYear(Integer sourceYear) {
        this.sourceYear = sourceYear;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Integer getTargetEndYear() {
        return targetEndYear;
    }

    public void setTargetEndYear(Integer targetEndYear) {
        this.targetEndYear = targetEndYear;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Integer getTargetNumber() {
        return targetNumber;
    }

    public void setTargetNumber(Integer targetNumber) {
        this.targetNumber = targetNumber;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Integer getTargetStartYear() {
        return targetStartYear;
    }

    public void setTargetStartYear(Integer targetStartYear) {
        this.targetStartYear = targetStartYear;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getTargetTitle() {
        return targetTitle;
    }

    public void setTargetTitle(String targetTitle) {
        this.targetTitle = targetTitle;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public Integer getTargetYear() {
        return targetYear;
    }

    public void setTargetYear(Integer targetYear) {
        this.targetYear = targetYear;
    }
}
