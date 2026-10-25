# Spring Day 2: extra practice (starter)

Six exercises. Exercises 1 to 4 follow the four topics of Day 2: each has three steps and each step has a test.
Exercises 5 and 6 go further than the slides and ask for more code. Exercise 5 is guided in steps. Exercise 6 is an
assignment with requirements and no steps: you work out the design yourself.
Work through the steps in order: run the test, read what it tells you, change the code, run it again.

How to run: import this folder as a Maven project in your IDE, or run `mvn test` in the folder.
To run one exercise: `mvn test -Dtest=PersistencePracticeTest` (or TransactionsPracticeTest,
ConsistencyPracticeTest, MessagingPracticeTest, BookingPracticeTest, ConfirmationPracticeTest).

## The exercises

| # | Topic | Package | Test class | What you work on |
|---|-------|---------|------------|------------------|
| 1 | Persistence context | `persistence` | `PersistencePracticeTest` | `ProductService` |
| 2 | Transactions | `transactions` | `TransactionsPracticeTest` | `TransferService`, `AttemptLogger`, `BatchTransferService` |
| 3 | Data consistency | `consistency` | `ConsistencyPracticeTest` | `Article`, `StockService`, `StockItemRepository`, `ArticleRepository` |
| 4 | Asynchronous integration | `messaging` | `MessagingPracticeTest` | `PaymentService`, `PaymentMessagePublisher`, `OutboxRelay`, `PaymentConsumer` |
| 5 | Retry on a conflict (guided) | `booking` | `BookingPracticeTest` | `BookingTransaction`, `BookingService`, `Flight` |
| 6 | Confirm only what is committed (assignment) | `confirmation` | `ConfirmationPracticeTest` | `OrderRegistrationService`, and new classes that you create |

Search the code for `TODO step` to find the places where you have to change something.

## What you see when you start

| Test | Starting state | Turns green after |
|------|----------------|-------------------|
| `PersistencePracticeTest.step1_oneTransaction_oneInstance` | red | step 1 |
| `PersistencePracticeTest.step2_dirtyChecking_noSaveNeeded` | red | step 2 |
| `PersistencePracticeTest.step3_readOnly_pricesAreRaised` | red | step 3 |
| `TransactionsPracticeTest.step1_checkedException_theTransferMustRollBack` | red | step 1 |
| `TransactionsPracticeTest.step2_requiresNew_theAttemptSurvivesTheRollback` | red | step 2 |
| `TransactionsPracticeTest.step3_batch_aFailedTransferDoesNotStopTheOthers` | red | step 3 |
| `ConsistencyPracticeTest.step1_optimisticLocking_theSecondSaveFails` | red | step 1 |
| `ConsistencyPracticeTest.step2_lastItem_theSecondOrderMustFail` | red | step 2 |
| `ConsistencyPracticeTest.step3_bulkUpdate_theNewPriceIsVisible` | red | step 3 |
| `MessagingPracticeTest.step1_failedPayment_sendsNoMessage` | red | step 1 |
| `MessagingPracticeTest.step1_successfulPayment_sendsOneMessage` | green (it must stay green) | |
| `MessagingPracticeTest.step2_brokerDown_theMessageIsNotLost` | red | step 2 |
| `MessagingPracticeTest.step3_idempotentConsumer_theSameMessageTwice` | red | step 3 |
| `BookingPracticeTest.step1_bookSeat_takesSeatsUntilSoldOut` | red | step 1 |
| `BookingPracticeTest.step2_conflict_theSlowerBookingFails` | red | step 2 |
| `BookingPracticeTest.step3_retry_bothCustomersGetASeat` | red | step 3 |
| `BookingPracticeTest.step3_retry_soldOutIsNotRetried` | red | step 3 |
| `ConfirmationPracticeTest.requirement1_validOrder_isStoredAndConfirmed` | red | requirement 1 |
| `ConfirmationPracticeTest.requirement2_overTheLimit_leavesNothingBehind` | red | requirement 2 |
| `ConfirmationPracticeTest.requirement3_failingCommit_sendsNoMail` | red | requirement 3 |
| `ConfirmationPracticeTest.requirement4_callerRollsBack_sendsNoMail` | green (it must stay green) | |

21 tests: 19 are red at the start, 2 are green (they must stay green). When you are done, all 21 are green.
In exercises 1 to 5 later steps build on earlier ones, so do them in order. The test names start with `step1`, `step2`, `step3`
and run in that order.

## Reading the console

* Every test prints `---- setup done: the test starts here ----`. Everything below that line belongs to the test.
  The SQL above it is only the setup.
* Every test ends with a line `after the test: ...` that shows the counts or values that were stored.
  The database lives in memory, so these lines are how you "look at the tables".
* SQL logging is on, so you see every `insert`, `update` and `select`. An `update` that you did not expect,
  or one that is missing, is often the answer.
* Transaction logging is on (`JpaTransactionManager`). Look for lines about creating a new transaction,
  participating in an existing one, suspending the current one, committing and rolling back.
  In exercises 2 and 3 those lines tell you what is really going on.

## Tip for Windows

Unpack the project in a short path, for example `C:\dev\spring-day2`. In a deep folder (such as a synced OneDrive
folder) Maven can fail with a message that a path is longer than Windows allows.

## If the application context does not start

All test classes share one Spring context. If a class that you wrote makes that context fail to start (for
example because of a wrong annotation), every test fails. Read the first error in the log: Spring usually says
exactly what is wrong.

## Exercise 6 in short

There are no steps and no TODO markers. The Javadoc above `OrderRegistrationService.register()` lists the four
requirements. You may change the constructor and create new classes. The four tests are the only judge, and the
console (SQL and transaction log) is your friend when a test stays red and you do not see why.
