package com.ballers.backend.repository;

import com.ballers.backend.model.GameParticipant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameParticipantRepository extends JpaRepository<GameParticipant, Long> {
}
