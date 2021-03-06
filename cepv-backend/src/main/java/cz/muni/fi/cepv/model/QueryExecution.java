package cz.muni.fi.cepv.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.hateoas.core.Relation;

import javax.persistence.*;
import java.util.Date;

/**
 * @author xgarcar
 */
@Entity
@Table(name = "QUERY_EXECUTION")
@Relation(collectionRelation = "queryExecutions")
public class QueryExecution extends CommonEntity{

    private Query query;
    private Date executionTime;

    public QueryExecution() {
    }

    public QueryExecution(Query query, Date executionTime) {
        this.query = query;
        this.executionTime = executionTime;
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "QUERY_ID", updatable = false)
    @JsonIgnore
    public Query getQuery() {
        return query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    @Column(name = "EXECUTION_TIME", nullable = false, updatable = false)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern=CommonEntity.JSON_FORMAT_DATE_PATTERN, timezone = "CET")
    public Date getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(Date executionTime) {
        this.executionTime = executionTime;
    }
}
