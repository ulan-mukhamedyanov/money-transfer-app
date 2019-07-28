# Money Transfer App
A RESTful API app for money transfers.

## Building the application
```
    mvn clean package
```
## Running tests
```
    mvn clean verify
```
## How to run the application
After having built the application there will appear the **moneytransfer-1.0-pack.jar** file.
It can be run as:
```
    java -jar moneytransfer-1.0-pack.jar
```
The port is: **8080**

## API Documentation

### Users

#### View all users

Request:
```
    GET localhost:8080/user/all
```

Response:
```
    HTTP 200
    [
        {
            "id": "0fdedc49-a757-4b65-a6ce-af3c4aa69f42",
            "name": "John Doe",
            "creationDateTime": "2019-01-01 00:00:00"
        },
        {
            "id": "1ce64136-11a7-41d6-a4d2-9275ccb7a758",
            "name": "Kate Willson",
            "creationDateTime": "2019-01-01 00:00:00"
        },
        {
            "id": "2573a818-448f-4b88-8055-6334ec056c27",
            "name": "Jeremy Price",
            "creationDateTime": "2019-01-01 00:00:00"
        },
        ...
    ]
```

#### View a user info by ID

Request:
```
    GET localhost:8080/user/info/{id}
```

Response:
```
    HTTP 200
    {
        "id": "0fdedc49-a757-4b65-a6ce-af3c4aa69f42",
        "name": "John Doe",
        "creationDateTime": "2019-01-01 00:00:00"
    }
```

#### Find users by name

Request:
```
    GET localhost:8080/user/find/{name}
```

Response:
```
    HTTP 200
    [
        {
            "id": "0fdedc49-a757-4b65-a6ce-af3c4aa69f42",
            "name": "John Doe",
            "creationDateTime": "2019-01-01 00:00:00"
        },
        {
            "id": "1ce64136-11a7-41d6-a4d2-9275ccb7a758",
            "name": "John Lock",
            "creationDateTime": "2019-01-01 00:00:00"
        },
        {
            "id": "2573a818-448f-4b88-8055-6334ec056c27",
            "name": "John Allen",
            "creationDateTime": "2019-01-01 00:00:00"
        },
        ...
    ]
```

#### Create a user

Request:
```
    POST localhost:8080/user/create
    {
        "name": "John Doe"
    }
```

Response:
```
    HTTP 201
    {
        "id": "0fdedc49-a757-4b65-a6ce-af3c4aa69f42",
        "name": "John Doe",
        "creationDateTime": "2019-01-01 00:00:00"
    }
```

#### Update a user

Request:
```
    PUT localhost:8080/user/edit/{id}
    {
        "name": "New Name"
    }
```

Response:
```
    HTTP 200
    {
        "id": "0fdedc49-a757-4b65-a6ce-af3c4aa69f42",
        "name": "New Name",
        "creationDateTime": "2019-01-01 00:00:00"
    }
```

#### Delete a user

Request:
```
    DELETE localhost:8080/user/delete/{id}
```

Response:
```
    HTTP 200
```

#### View user accounts

Request:
```
    GET localhost:8080/user/accounts/{id}
```

Response:
```
    HTTP 200
    [
        {
            "id": "0fdedc49-a757-4b65-a6ce-af3c4aa69f42",
            "owner": {
                "id": "1ce64136-11a7-41d6-a4d2-9275ccb7a758",
                "name": "John Lock",
                "creationDateTime": "2019-01-01 00:00:00"
            },
            "name": "main account",
            "creationDateTime": "2019-01-01 00:00:00",
            "balance": "415.22",
            "currency": "USD",
            "active": true
        },
        {
            "id": "1ce64136-11a7-41d6-a4d2-9275ccb7a758",
            "owner": {
                "id": "1ce64136-11a7-41d6-a4d2-9275ccb7a758",
                "name": "John Lock",
                "creationDateTime": "2019-01-01 00:00:00"
            },
            "name": "saving account",
            "creationDateTime": "2019-01-01 00:00:00",
            "balance": "12143.22",
            "currency": "USD",
            "active": true
        }
    ]
```

#### Create an account for a user

Request:
```
    POST localhost:8080/user/create-account
    {
        "ownerId": "0fdedc49-a757-4b65-a6ce-af3c4aa69f42",
        "name": "new account",
        "balance": "0.00",
        "currency": "USD",
        "active": true
    }
```

Response:
```
    HTTP 201
    {
        "id": "2573a818-448f-4b88-8055-6334ec056c27",
        "owner": {
            "id": "1ce64136-11a7-41d6-a4d2-9275ccb7a758",
            "name": "John Lock",
            "creationDateTime": "2019-01-01 00:00:00"
        },
        "name": "new account",
        "creationDateTime": "2019-01-01 00:00:00",
        "balance": "0.00",
        "currency": "USD",
        "active": true
    }
```


### Accounts

#### View an account info by ID

Request:
```
    GET localhost:8080/account/info/{id}
```
Response:
```
    HTTP 200
    {
        "id": "2573a818-448f-4b88-8055-6334ec056c27",
        "owner": {
            "id": "1ce64136-11a7-41d6-a4d2-9275ccb7a758",
            "name": "John Lock",
            "creationDateTime": "2019-01-01 00:00:00"
        },
        "name": "new account",
        "creationDateTime": "2019-01-01 00:00:00",
        "balance": "0.00",
        "currency": "USD",
        "active": true
    }
```

#### Update an account

Request:
```
    PUT localhost:8080/account/edit/{id}
    {
        "ownerId": "0fdedc49-a757-4b65-a6ce-af3c4aa69f42",
        "name": "modified account",
        "balance": "0.00",
        "currency": "USD",
        "active": true
    }
```
Response:
```
    HTTP 200
    {
        "id": "2573a818-448f-4b88-8055-6334ec056c27",
        "owner": {
            "id": "0fdedc49-a757-4b65-a6ce-af3c4aa69f42",
            "name": "John Doe",
            "creationDateTime": "2019-01-01 00:00:00"
        },
        "name": "modified account",
        "creationDateTime": "2019-01-01 00:00:00",
        "balance": "0.00",
        "currency": "USD",
        "active": true
    }
```

#### Delete an account

Request:
```
    DELETE localhost:8080/account/delete/{id}
```
Response:
```
    HTTP 200
```

#### Activate an account

Request:
```
    PUT localhost:8080/account/activate/{id}
```
Response:
```
    HTTP 200
    {
        "id": "2573a818-448f-4b88-8055-6334ec056c27",
        "owner": {
            "id": "0fdedc49-a757-4b65-a6ce-af3c4aa69f42",
            "name": "John Doe",
            "creationDateTime": "2019-01-01 00:00:00"
        },
        "name": "modified account",
        "creationDateTime": "2019-01-01 00:00:00",
        "balance": "0.00",
        "currency": "USD",
        "active": true
    }
```

#### Deactivate an account

Request:
```
    PUT localhost:8080/account/deactivate/{id}
```
Response:
```
    HTTP 200
    {
        "id": "2573a818-448f-4b88-8055-6334ec056c27",
        "owner": {
            "id": "0fdedc49-a757-4b65-a6ce-af3c4aa69f42",
            "name": "John Doe",
            "creationDateTime": "2019-01-01 00:00:00"
        },
        "name": "modified account",
        "creationDateTime": "2019-01-01 00:00:00",
        "balance": "0.00",
        "currency": "USD",
        "active": false
    }
```

#### Deposit

Request:
```
    POST localhost:8080/account/deposit
    {
        "targetAccountId": "2573a818-448f-4b88-8055-6334ec056c27",
        "amount": "1000.00"
    }
```
Response:
```
    HTTP 200
    {
        "id": "a7425193-94f4-47e9-80ff-c2cf2558598f",
        "sourceAccount": {
            "id": "00000000-0000-0000-0000-000000000000",
            "name": "Account for deposit and withdrawal",
            "creationDateTime": "2019-01-01 00:00:00",
            "active": true
        },
        "destinationAccount": {
            "id": "2573a818-448f-4b88-8055-6334ec056c27",
            "owner": {
                "id": "0fdedc49-a757-4b65-a6ce-af3c4aa69f42",
                "name": "John Doe",
                "creationDateTime": "2019-01-01 00:00:00"
            },
            "name": "modified account",
            "creationDateTime": "2019-01-01 00:00:00",
            "balance": "1000.00",
            "currency": "USD",
            "active": true
        },
        "amount": "1000.00",
        "currency": "USD",
        "comment": "Deposit",
        "status": "EXECUTED",
        "creationDateTime": "2019-01-01 00:00:00",
        "executionDateTime": "2019-01-01 00:00:00"
    }
```

#### Withdraw

Request:
```
    POST localhost:8080/account/withdraw
    {
        "targetAccountId": "2573a818-448f-4b88-8055-6334ec056c27",
        "amount": "1000.00"
    }
```
Response:
```
    HTTP 200
    {
        "id": "a7425193-94f4-47e9-80ff-c2cf2558598f",
        "sourceAccount": {
            "id": "2573a818-448f-4b88-8055-6334ec056c27",
            "owner": {
                "id": "0fdedc49-a757-4b65-a6ce-af3c4aa69f42",
                "name": "John Doe",
                "creationDateTime": "2019-01-01 00:00:00"
            },
            "name": "modified account",
            "creationDateTime": "2019-01-01 00:00:00",
            "balance": "0.00",
            "currency": "USD",
            "active": true
        },
        "destinationAccount": {
            "id": "00000000-0000-0000-0000-000000000000",
            "name": "Account for deposit and withdrawal",
            "creationDateTime": "2019-01-01 00:00:00",
            "active": true
        },
        "amount": "1000.00",
        "currency": "USD",
        "comment": "Withdrawal",
        "status": "EXECUTED",
        "creationDateTime": "2019-01-01 00:00:00",
        "executionDateTime": "2019-01-01 00:00:00"
    }
```

#### Transfer

Request:
```
    POST localhost:8080/account/transfer
    {
        "sourceAccount": "2573a818-448f-4b88-8055-6334ec056c27",
        "destinationAccount": "1ce64136-11a7-41d6-a4d2-9275ccb7a758",
        "amount": "1000.00",
        "currency": "USD",
        "comment": "Transfer to John Lock"
    }
```
Response:
```
    HTTP 200
    {
        "id": "a7425193-94f4-47e9-80ff-c2cf2558598f",
        "sourceAccount": {
            "id": "2573a818-448f-4b88-8055-6334ec056c27",
            "owner": {
                "id": "0fdedc49-a757-4b65-a6ce-af3c4aa69f42",
                "name": "John Doe",
                "creationDateTime": "2019-01-01 00:00:00"
            },
            "name": "modified account",
            "creationDateTime": "2019-01-01 00:00:00",
            "balance": "0.00",
            "currency": "USD",
            "active": true
        },
        "destinationAccount": {
            "id": "1ce64136-11a7-41d6-a4d2-9275ccb7a758",
                "owner": {
                    "id": "1ce64136-11a7-41d6-a4d2-9275ccb7a758",
                    "name": "John Lock",
                    "creationDateTime": "2019-01-01 00:00:00"
                },
                "name": "saving account",
                "creationDateTime": "2019-01-01 00:00:00",
                "balance": "13143.22",
                "currency": "USD",
                "active": true
        },
        "amount": "1000.00",
        "currency": "USD",
        "comment": "Transfer to John Lock",
        "status": "EXECUTED",
        "creationDateTime": "2019-01-01 00:00:00",
        "executionDateTime": "2019-01-01 00:00:00"
    }
```

#### View account transactions

Request:
```
    GET localhost:8080/account/transactions/{id}
```
Response:
```
    HTTP 200
    [
        {
            "id": "a7425193-94f4-47e9-80ff-c2cf2558598f",
            "sourceAccount": {
                "id": "2573a818-448f-4b88-8055-6334ec056c27",
                "owner": {
                    "id": "0fdedc49-a757-4b65-a6ce-af3c4aa69f42",
                    "name": "John Doe",
                    "creationDateTime": "2019-01-01 00:00:00"
                },
                "name": "modified account",
                "creationDateTime": "2019-01-01 00:00:00",
                "balance": "0.00",
                "currency": "USD",
                "active": true
            },
            "destinationAccount": {
                "id": "1ce64136-11a7-41d6-a4d2-9275ccb7a758",
                    "owner": {
                        "id": "1ce64136-11a7-41d6-a4d2-9275ccb7a758",
                        "name": "John Lock",
                        "creationDateTime": "2019-01-01 00:00:00"
                    },
                    "name": "saving account",
                    "creationDateTime": "2019-01-01 00:00:00",
                    "balance": "13143.22",
                    "currency": "USD",
                    "active": true
            },
            "amount": "1000.00",
            "currency": "USD",
            "comment": "Transfer to John Lock",
            "status": "EXECUTED",
            "creationDateTime": "2019-01-01 00:00:00",
            "executionDateTime": "2019-01-01 00:00:00"
        }
    ]
```

### Transactions

#### View transaction info

Request:
```
    GET localhost:8080/transaction/info/{id}
```
Response:
```
    HTTP 200
    {
        "id": "a7425193-94f4-47e9-80ff-c2cf2558598f",
        "sourceAccount": {
            "id": "2573a818-448f-4b88-8055-6334ec056c27",
            "owner": {
                "id": "0fdedc49-a757-4b65-a6ce-af3c4aa69f42",
                "name": "John Doe",
                "creationDateTime": "2019-01-01 00:00:00"
            },
            "name": "modified account",
            "creationDateTime": "2019-01-01 00:00:00",
            "balance": "0.00",
            "currency": "USD",
            "active": true
        },
        "destinationAccount": {
            "id": "1ce64136-11a7-41d6-a4d2-9275ccb7a758",
                "owner": {
                    "id": "1ce64136-11a7-41d6-a4d2-9275ccb7a758",
                    "name": "John Lock",
                    "creationDateTime": "2019-01-01 00:00:00"
                },
                "name": "saving account",
                "creationDateTime": "2019-01-01 00:00:00",
                "balance": "13143.22",
                "currency": "USD",
                "active": true
        },
        "amount": "1000.00",
        "currency": "USD",
        "comment": "Transfer to John Lock",
        "status": "EXECUTED",
        "creationDateTime": "2019-01-01 00:00:00",
        "executionDateTime": "2019-01-01 00:00:00"
    }
```
