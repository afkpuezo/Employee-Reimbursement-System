# Project-1: Employee Reimbursment System (ERS)

## Executive Summary
* The Expense Reimbursement System (ERS) will manage the process of reimbursing employees for expenses incurred while on company time. 
* All employees in the company can login and submit requests for reimbursement and view their past tickets and pending requests. 
* Finance managers can log in and view all reimbursement requests and past history for all employees in the company. 
* Finance managers are authorized to approve and deny requests for expense reimbursement.

#### Employee User Stories 
- An Employee can login [x]
- An Employee can view the Employee Homepage [x]
- An Employee can logout [x]
- An Employee can submit a reimbursement request [x]
- An Employee can upload an image of his/her receipt as part of the reimbursement request (extra credit)
- An Employee can view their pending reimbursement requests [x]
- An Employee can view their resolved reimbursement requests [x]
- An Employee can view their information [x]
- An Employee can update their information [x]

#### Manager User Stories
- A Manager can login [x]
- A Manager can view the Manager Homepage [x]
- A Manager can logout [x]
- A Manager can approve/deny pending reimbursement requests [x]
- A Manager can view all pending requests from all employees [x]
- A Manager can view images of the receipts from reimbursement requests (extra credit) [x]
- A Manager can view all resolved requests from all employees and see which manager resolved it [x]
- A Manager can view all Employees [x]
- A Manager can view reimbursement requests from a single Employee [x]

**Reimbursement Types**

Employees must select the type of reimbursement as: LODGING, TRAVEL, FOOD, or OTHER.

## Technical Requirements

* The back-end system shall use **Hibernate** to connect to an **AWS RDS Postgres database**.  [x]
* The application shall deploy onto a Tomcat Server. [x]
* The middle tier shall use Servlet technology for dynamic Web application development. [x]
* The front-end view shall use HTML/JavaScript to make an application that can call server-side components. [x]
* Passwords shall be encrypted in Java and securely stored in the database. [x]
* The middle tier shall follow proper layered architecture, have reasonable (~70%) test coverage of the service layer [x]

