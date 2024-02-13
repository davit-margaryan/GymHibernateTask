INSERT INTO User (first_name, last_name, username, password, is_active)
VALUES ('Trainee1', 'User1', 'trainee1', 'password1', true),
       ('Trainee2', 'User2', 'trainee2', 'password2', true),
       ('Trainee3', 'User3', 'trainee3', 'password3', true),
       ('Trainee4', 'User4', 'trainee4', 'password4', true),
       ('Trainee5', 'User5', 'trainee5', 'password5', true),
       ('Trainer1', 'User6', 'trainer1', 'password1', true),
       ('Trainer2', 'User7', 'trainer2', 'password2', true),
       ('Trainer3', 'User8', 'trainer3', 'password3', true),
       ('Trainer4', 'User9', 'trainer4', 'password4', true),
       ('Trainer5', 'User10', 'trainer5', 'password5', true),
       ('Davo', 'Davo', 'admin', '$2a$10$Sit1CmXANuZUxmUk3WdAY.9dJqJVuU9uB1zJZ7Kb/1uc1HET8YTAi', true);


INSERT INTO Trainee (date_of_birth, address, user_id)
VALUES ('1991-04-04', 'Address1', (SELECT id FROM User WHERE username = 'trainee1')),
       ('1993-05-05', 'Address2', (SELECT id FROM User WHERE username = 'trainee2')),
       ('1994-06-06', 'Address3', (SELECT id FROM User WHERE username = 'trainee3')),
       ('1996-07-07', 'Address4', (SELECT id FROM User WHERE username = 'trainee4')),
       ('1997-08-08', 'Address5', (SELECT id FROM User WHERE username = 'trainee5'));

INSERT INTO training_type (training_type_name)
VALUES ('Cycling'),
       ('Aerobics'),
       ('Bodybuilding'),
       ('Boxing'),
       ('Martial Arts'),
       ('Rowing'),
       ('Tai Chi'),
       ('Running'),
       ('Swimming'),
       ('Dancing');

INSERT INTO Trainer (user_id, specialization)
VALUES ((SELECT id FROM User WHERE username = 'trainer1'),
        (SELECT id FROM Training_Type WHERE training_type_name = 'Cycling')),
       ((SELECT id FROM User WHERE username = 'trainer2'),
        (SELECT id FROM Training_Type WHERE training_type_name = 'Aerobics')),
       ((SELECT id FROM User WHERE username = 'trainer3'),
        (SELECT id FROM Training_Type WHERE training_type_name = 'Bodybuilding')),
       ((SELECT id FROM User WHERE username = 'trainer4'),
        (SELECT id FROM Training_Type WHERE training_type_name = 'Boxing')),
       ((SELECT id FROM User WHERE username = 'trainer5'),
        (SELECT id FROM Training_Type WHERE training_type_name = 'Martial Arts'));