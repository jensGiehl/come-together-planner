package de.agiehl.cometogether.domain.repository;

import de.agiehl.cometogether.domain.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    List<Vote> findBySurveyId(Long surveyId);

    List<Vote> findByUserIdAndSurveyId(Long userId, Long surveyId);
}
