-- Insert three User records
INSERT INTO User (first_name, last_name, username, password, is_active)
VALUES
    ('Trainee', 'User1', 'trainee1', 'password1', true),
    ('Trainee', 'User2', 'trainee2', 'password2', true),
    ('Trainee', 'User3', 'trainee3', 'password3', true),
    ('Trainer', 'User1', 'trainer1', 'password1', true),
    ('Trainer', 'User2', 'trainer2', 'password2', true),
    ('Trainer', 'User3', 'trainer3', 'password3', true),
    ('Davo', 'Davo', 'admin', '$2a$10$Sit1CmXANuZUxmUk3WdAY.9dJqJVuU9uB1zJZ7Kb/1uc1HET8YTAi', true);

-- Insert three Trainee records
INSERT INTO Trainee (date_of_birth, address, user_id)
VALUES ('1990-01-01', 'Address1', (SELECT id FROM User WHERE username = 'trainee1')),
       ('1992-02-02', 'Address2', (SELECT id FROM User WHERE username = 'trainee2')),
       ('1995-03-03', 'Address3', (SELECT id FROM User WHERE username = 'trainee3'));

-- Insert five TrainingType records
INSERT INTO training_type (training_type_name)
VALUES ('Cardio'),
       ('Strength Training'),
       ('Yoga'),
       ('Pilates'),
       ('CrossFit');


-- Insert three Trainer records with assigned TrainingType
INSERT INTO Trainer (user_id, specialization)
VALUES ((SELECT id FROM User WHERE username = 'trainer1'),
        (SELECT id FROM Training_Type WHERE training_type_name = 'Cardio')),
       ((SELECT id FROM User WHERE username = 'trainer2'),
        (SELECT id FROM Training_Type WHERE training_type_name = 'Strength Training')),
       ((SELECT id FROM User WHERE username = 'trainer3'),
        (SELECT id FROM Training_Type WHERE training_type_name = 'Yoga'));