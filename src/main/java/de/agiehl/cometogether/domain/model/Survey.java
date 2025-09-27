package de.agiehl.cometogether.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@ValidSurveyDates
public class Survey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String publicId;

    @Column(nullable = false)
    private String title;

    @Lob
    @NotEmpty(message = "Description cannot be empty.")
    private String description;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "survey_weekdays")
    @Column(name = "weekday")
    private Set<java.time.DayOfWeek> weekdays;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "survey")
    @OrderColumn(name = "exclusion_order")
    private List<DateExclusion> exclusions = new ArrayList<>();

    public void addExclusion(DateExclusion exclusion) {
        exclusions.add(exclusion);
        exclusion.setSurvey(this);
    }

    public void removeExclusion(DateExclusion exclusion) {
        exclusions.remove(exclusion);
        exclusion.setSurvey(null);
    }
}
