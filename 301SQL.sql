DROP TABLE IF EXISTS Users;
DROP TABLE IF EXISTS Events;
CREATE TABLE Users(
	ID INTEGER PRIMARY KEY AUTOINCREMENT,
	username TEXT NOT NULL,
	password TEXT NOT NULL,
	blockedBy TEXT DEFAULT "[]",
	blocked TEXT DEFAULT "[]",
    	invited TEXT DEFAULT "[]",
    	accepted TEXT DEFAULT "[]",
	unavailableDates TEXT DEFAULT "[]"
);
INSERT INTO Users('username', 'password') VALUES
  ('ifmmp', 'fbfb386efea67e816f2dda0a8c94a98eb203757aebb3f55f183755a192d44467'),
  ('upnnz', 'fbfb386efea67e816f2dda0a8c94a98eb203757aebb3f55f183755a192d44467');

INSERT INTO Users('username', 'password', 'invited', 'accepted') VALUES
  ('uspkbo', 'fbfb386efea67e816f2dda0a8c94a98eb203757aebb3f55f183755a192d44467', '["testevent"]', '["testevent"]');

INSERT INTO Users('username', 'password', 'blockedBy', 'blocked', 'accepted', 'unavailableDates') VALUES
 ('uftuCmpdlfs', 'fbfb386efea67e816f2dda0a8c94a98eb203757aebb3f55f183755a192d44467', '["gjmmfs"]', '["gjmmfs"]', '["testevent"]','["August 5, 2021", "November 12, 2021"]');

CREATE Table Events(
	ID INTEGER PRIMARY KEY AUTOINCREMENT,
	eventname TEXT NOT NULL,
	hostname TEXT NOT NULL,
	date TEXT TEXT DEFAULT "[]",
	invitees TEXT DEFAULT "[]",
	accepted TEXT DEFAULT "[]",
	declined TEXT DEFAULT "[]",
	preferences TEXT DEFAULT "[]",
	stage TEXT NOT NULL
);

INSERT INTO Events('eventname', 'hostname', 'invitees', 'stage') VALUES
 ('testevent', 'testhost', '["uspkbo"]', 'pending');

