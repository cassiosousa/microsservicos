package br.com.microservices.orchestrated.inventoryservicekotlin.kafka

val orchestratorPayload =  """
            {
                "id": "64429e9a7a8b646915b37360",
                "transactionId": "1682087576536_99d2ca6c-f074-41a6-92e0-21700148b519",
                "orderId": "64429e987a8b646915b3735f",
                "payload": {
                    "id": "64429e987a8b646915b3735f",
                    "products": [
                        {
                            "product": {
                                "code": "COMIC_BOOKS",
                                "unitValue": 15.5
                            },
                            "quantity": 1
                        },
                        {
                            "product": {
                                "code": "BOOKS",
                                "unitValue": 9.9
                            },
                            "quantity": 1
                        }
                    ],
                    "totalAmount": 56.4,
                    "totalItems": 4,
                    "createdAt": "2023-04-21T14:32:56.335943085",
                    "transactionId": "1682087576536_99d2ca6c-f074-41a6-92e0-21700148b519"
                },
                "source": "ORCHESTRATOR",
                "status": "SUCCESS",
                "createdAt": "2023-04-21T14:32:58.28"
            }
        """.trimIndent();

val inventorySuccessPayload =  """
            {
                "id": "64429e9a7a8b646915b37360",
                "transactionId": "1682087576536_99d2ca6c-f074-41a6-92e0-21700148b519",
                "orderId": "64429e987a8b646915b3735f",
                "payload": {
                    "id": "64429e987a8b646915b3735f",
                    "products": [
                        {
                            "product": {
                                "code": "COMIC_BOOKS",
                                "unitValue": 15.5
                            },
                            "quantity": 1
                        },
                        {
                            "product": {
                                "code": "BOOKS",
                                "unitValue": 9.9
                            },
                            "quantity": 1
                        }
                    ],
                    "totalAmount": 56.4,
                    "totalItems": 4,
                    "createdAt": "2023-04-21T14:32:56.335943085",
                    "transactionId": "1682087576536_99d2ca6c-f074-41a6-92e0-21700148b519"
                },
                "source": "ORCHESTRATOR",
                "status": "SUCCESS",
                "createdAt": "2023-04-21T14:32:58.28"
            }
        """.trimIndent();

val inventoryFailPayload =  """
            {
                "id": "64429e9a7a8b646915b37360",
                "transactionId": "1682087576536_99d2ca6c-f074-41a6-92e0-21700148b519",
                "orderId": "64429e987a8b646915b3735f",
                "payload": {
                    "id": "64429e987a8b646915b3735f",
                    "products": [
                        {
                            "product": {
                                "code": "COMIC_BOOKS",
                                "unitValue": 15.5
                            },
                            "quantity": 0
                        },
                        {
                            "product": {
                                "code": "BOOKS",
                                "unitValue": 9.9
                            },
                            "quantity": 0
                        }
                    ],
                    "totalAmount": 56.4,
                    "totalItems": 4,
                    "createdAt": "2023-04-21T14:32:56.335943085",
                    "transactionId": "1682087576536_99d2ca6c-f074-41a6-92e0-21700148b519"
                },
                "source": "ORCHESTRATOR",
                "status": "SUCCESS",
                "createdAt": "2023-04-21T14:32:58.28"
            }
        """.trimIndent();