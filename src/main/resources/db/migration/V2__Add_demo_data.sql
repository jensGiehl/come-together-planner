INSERT INTO users (name, access_codeword) VALUES ('Max Mustermann', 'max');
INSERT INTO users (name, access_codeword) VALUES ('Erika Mustermann', 'erika');

-- Let the database generate the ID
INSERT INTO survey (public_id, title, description, start_date, end_date) VALUES ('demo', 'Team-Event Planung', 'Lasst uns einen Termin für unser nächstes Team-Event finden!', '2025-12-01', '2025-12-31');

-- Add weekdays for the demo survey
INSERT INTO survey_weekdays (survey_id, weekday) VALUES (1, 'SATURDAY');
INSERT INTO survey_weekdays (survey_id, weekday) VALUES (1, 'SUNDAY');
