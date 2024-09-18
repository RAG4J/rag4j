package org.rag4j.rag.generation.knowledge;

import lombok.*;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@Getter
@Builder
@EqualsAndHashCode
@ToString
public class Knowledge {
    private String subject;
    private String description;
}
