package org.rag4j.rag.store;

import lombok.*;

import java.util.Date;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
@Builder
@EqualsAndHashCode
@ToString
public class Metadata {
    private String name;
    private Date creationDate;
    private String embedder;
    private String supplier;
    private String model;

}
