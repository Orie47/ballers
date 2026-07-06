package com.ballers.backend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

// Marks this class as a JPA entity: Hibernate maps it to a table (named "users" below),
// one column per field, and manages the table via ddl-auto.
@Entity
// "user" is a reserved word in Postgres's SQL grammar (it's tied to the CURRENT_USER
// keyword), so a table literally named "user" causes syntax errors on unquoted DDL - every
// CREATE TABLE/ALTER TABLE Hibernate generates for it, and every foreign key pointing at it,
// fails. Naming the table "users" sidesteps the clash entirely.
@Table(name = "users")
// Lombok generates a getter and setter for every field at compile time
// (getUsername()/setUsername(), etc.) so we don't hand-write them.
@Getter
@Setter
public class User {

    // Primary key. IDENTITY means Postgres auto-increments it on every insert.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String gender;

    // Maps to column "height_cm".
    private Integer heightCm;

    // Stored in the database as the enum's name, e.g. "BEGINNER", instead of a number.
    // Storing the name is safer than storing a position number (ORDINAL), because
    // reordering/inserting enum values later can't silently corrupt existing rows.
    @Enumerated(EnumType.STRING)
    private SkillLevel skillLevel;

    // Ratings live on a separate PlayerSportRating row per sport, not here - see that class
    // for why a single overall rating on User doesn't make sense.

    // User's last known location, used for "nearby games" search (see GameService).
    private Double lat;

    private Double lng;
}
