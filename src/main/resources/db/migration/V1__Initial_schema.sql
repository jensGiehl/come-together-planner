CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    access_codeword VARCHAR_IGNORECASE(255) NOT NULL UNIQUE
);

CREATE TABLE survey (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    public_id VARCHAR(255) NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    description CLOB,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL
);

CREATE TABLE survey_weekdays (
    survey_id BIGINT NOT NULL,
    weekday VARCHAR(255) NOT NULL,
    FOREIGN KEY (survey_id) REFERENCES survey(id)
);

CREATE TABLE date_exclusion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    survey_id BIGINT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    exclusion_order INTEGER,
    FOREIGN KEY (survey_id) REFERENCES survey(id)
);

CREATE TABLE vote (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    survey_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    date DATE NOT NULL,
    status VARCHAR(255) NOT NULL,
    FOREIGN KEY (survey_id) REFERENCES survey(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    UNIQUE (survey_id, user_id, date)
);
