# Product Requirements Document (PRD)

**Project:** InternFlow

## 1. Document Control

- **Document Title:** InternFlow Product Requirements Document (PRD)
- **Version:** 1.0
- **Status:** Draft — Pending Review
- **Last Updated:** 2026-10-14
- **Purpose:** Define InternFlow's product vision, goals, scope, users, workflows, high-level requirements, constraints, risks, assumptions, and success criteria for the MVP. This document establishes the product baseline for subsequent requirements and technical planning.

## 2. Executive Summary

InternFlow is an internship management platform designed to manage the internship lifecycle from registration through verified completion. It aims to replace fragmented tracking methods—including spreadsheets, messages, emails, and documents—with a centralized, traceable workflow.

**Key characteristics:**

- **Purpose:** Centralize internship-related activities, decisions, and documentation.
- **Primary users:** Students, Faculty Coordinators, and College Administrators.
- **Optional future users:** Company Mentors, whose functionality is excluded from the initial MVP.
- **Initial deployment:** One college department or internship cohort.
- **Planned development duration:** 12 weeks.
- **Architecture:** Modular monolith.
- **Primary outcome:** A demonstrable system that supports internship tracking, weekly reporting, faculty review, compliance monitoring, and verified completion.

## 3. Problem Statement

Internship management can involve multiple disconnected tools and manual processes. InternFlow is intended to address the following problem areas:

- **Scattered documentation:** Internship-related documents may be distributed across multiple locations, making them difficult to locate and track consistently.
- **Inconsistent weekly reporting:** Variations in reporting formats and submission tracking can reduce visibility into student progress.
- **Difficult-to-track faculty reviews:** Review decisions, correction requests, and approvals need a consistent, traceable workflow.
- **Missed deadlines:** Without centralized tracking and reminders, important deadlines may be overlooked.
- **Unclear compliance status:** Students and coordinators need visibility into whether mandatory internship requirements have been satisfied.
- **Difficult completion verification:** Internship completion requires a structured process that records outstanding requirements and authorized approval.

These are the problem areas the product is designed to address. Their real-world prevalence and impact have not yet been measured.

## 4. Product Vision

**Vision statement:** Provide a centralized, transparent, and traceable internship management workflow that enables consistent reporting, structured faculty review, and verifiable completion tracking.

**Value to deliver:**

- A centralized source of truth for internship data and documents.
- Clear and repeatable workflows for all participants.
- Traceable decisions and actions through audit history.
- Structured compliance tracking against defined requirements.
- Clear visibility into student progress and outstanding work.
- A reliable demonstration of the complete internship lifecycle.

## 5. Goals and Non-Goals

### 5.1 Goals

1. **Streamline the workflow:** Replace fragmented tracking methods with a unified internship lifecycle workflow.
2. **Enable structured reporting:** Support weekly reports and work logs with faculty review, correction requests, and resubmission.
3. **Ensure verifiable completion:** Verify completion against mandatory requirements and authorized approval.
4. **Maintain auditability:** Preserve important actions and decisions for traceability.
5. **Enforce role-based access:** Restrict actions and information according to user roles.
6. **Support faculty oversight:** Enable Faculty Coordinators to review submissions, provide feedback, approve reports, and monitor compliance.
7. **Deliver a focused MVP:** Demonstrate the core workflows within the planned 12-week development period for one department or cohort.

### 5.2 Non-Goals

InternFlow is not intended to be:

- A complete college ERP.
- A job portal.
- An attendance-only application.
- Merely a document upload page.
- Merely a certificate generator.
- A microservices-based system.

The selected architecture is a modular monolith.

## 6. Target Users and Personas

### 6.1 Students

**Role:** Primary participants in the internship lifecycle.

**Goals:**
- Register internship details.
- Submit required documents.
- Submit weekly reports and work logs.
- Respond to faculty correction requests.
- Track progress toward internship completion.

**Responsibilities:**
- Provide accurate information.
- Submit required work within applicable deadlines.
- Address faculty feedback and resubmit work when necessary.

**Key interactions:** Internship registration, document submission, weekly reporting, report resubmission, and compliance-status review.

### 6.2 Faculty Coordinators

**Role:** Academic oversight and review authority.

**Goals:**
- Review student submissions.
- Provide feedback and request corrections.
- Monitor progress and compliance.
- Verify completion against mandatory requirements.

**Responsibilities:**
- Review internship information and required documents.
- Review weekly reports and work logs.
- Approve reports or request corrections.
- Authorize completion when applicable requirements are satisfied.

**Established authority:** Faculty Coordinators approve weekly reports.

The FRD must specify the exact permissions and state-transition rules for report review, correction requests, resubmission, approval, and completion verification.

### 6.3 College Administrators

**Role:** System administration and institutional oversight.

**Goals:**
- Manage users and cohorts.
- Configure internship requirements.
- Monitor overall progress.
- Support consistent operation of the platform.

**Responsibilities:**
- User and role administration.
- Cohort management.
- Requirement configuration.
- High-level monitoring and reporting.

The FRD must define which administrative actions are permitted, including requirement changes, user-role assignments, and access to student records.

### 6.4 Company Mentors

**Status:** Optional post-MVP functionality.

Company Mentor evaluations and related workflows are not required for the initial MVP. Their permissions, access boundaries, and interactions will be defined if this functionality is considered for a future release.

## 7. Product Scope and Feature Priorities

### 7.1 Essential MVP Functionality

The MVP includes the following 13 essential modules.

#### 1. Authentication and Role-Based Access

- Secure authentication and role-based permissions.
- Access appropriate to the responsibilities of each user role.

#### 2. User Management

- Manage user accounts and role assignments.
- Support the Student, Faculty Coordinator, and College Administrator roles.

#### 3. Internship Management

- Record and manage internship details.
- Track internship lifecycle state.

#### 4. Cohort Management

- Configure and manage student cohorts and their associated requirements.
- Support the initial deployment scope of one department or cohort.

#### 5. Document Management

- Support submission, storage, access control, and review of required documents.
- Use private object storage.

#### 6. Weekly Reports and Work Logs

- Support report submission, review, correction requests, resubmission, and approval.
- Prioritize work logs over attendance tracking.

#### 7. Faculty Review Workflow

- Provide structured review, feedback, correction requests, and approval.
- Preserve review decisions and their history.

#### 8. Compliance Engine

- Track mandatory internship requirements.
- Calculate and update compliance status based on applicable requirements and workflow outcomes.

#### 9. Role-Specific Dashboards

- Present information and actions relevant to each user's role.
- Provide visibility into progress, pending work, and compliance where appropriate.

#### 10. Notifications

- Support email and in-app notifications for relevant workflow events and deadlines.

#### 11. Completion Verification

- Record completion only when mandatory requirements are satisfied and authorized approval has been granted.

#### 12. Audit History

Minimum expected MVP functionality:

- Record important actions and decisions related to core internship workflows.
- Associate recorded events with the relevant user and affected record where applicable.
- Preserve sufficient event information to establish what action occurred and when.
- Allow authorized users to review relevant history.
- Prevent ordinary users from modifying or deleting recorded audit events through normal application workflows.

The exact event list, retention rules, and detailed access permissions must be defined in the FRD and supporting technical documents.

#### 13. Reports and CSV Exports

Minimum expected MVP functionality:

- Provide basic operational reporting for internship progress and compliance.
- Support authorized users in reviewing relevant student or cohort information.
- Export supported report data to CSV.
- Apply role-based access restrictions to report data and exports.
- Ensure exported information respects the requesting user's data-access permissions.

The specific report types, filters, columns, and export rules must be defined in the FRD. Advanced analytics and PDF reporting are not required for the initial MVP.

### 7.2 Optional Post-MVP Functionality

The following features are explicitly excluded from mandatory initial MVP scope:

- Company Mentor evaluations.
- Attendance tracking.
- Advanced analytics and PDF reports.
- Additional notification channels.
- Multi-department or multi-college support.
- External college-system integrations.

Work logs take priority over attendance. Optional functionality must not displace essential MVP workflows or be treated as a mandatory release requirement.

### 7.3 Implementation Priority

The first implementation priority is the complete report submission and faculty review workflow defined in Section 9.

After that workflow is implemented and verified, development should proceed through the remaining essential modules according to the 12-week roadmap.

All 13 modules remain part of the planned MVP scope. Prioritization determines implementation order; it does not remove essential modules from the release scope.

## 8. User Workflows

The principal internship lifecycle is:

1. **Cohort configuration:** A College Administrator configures a cohort and its requirements.
2. **Internship registration:** A Student registers internship details.
3. **Document submission:** The Student submits required documents.
4. **Initial review:** An authorized Faculty Coordinator reviews the internship details and documents.
5. **Weekly reporting:** The Student submits weekly reports and work logs.
6. **Faculty review:** The Faculty Coordinator approves a report or requests corrections.
7. **Resubmission:** The Student revises and resubmits the report when corrections are requested.
8. **Compliance calculation:** The system tracks outstanding mandatory requirements and updates compliance status.
9. **Completion verification:** An authorized Faculty Coordinator verifies the applicable completion requirements and approves completion.
10. **Record preservation:** Completed records and their history remain available according to defined retention rules.

The specific retention duration and rules remain to be defined.

## 9. First Major Milestone

**Milestone:** Complete the report submission, correction request, resubmission, approval, and compliance-status update workflow.

The end-to-end flow is:

**Student submits a report → Faculty requests corrections → Student resubmits → Faculty approves → Compliance status updates.**

### 9.1 Observable Acceptance Outcomes

- A Student can submit a weekly report.
- A Faculty Coordinator can request corrections to that report.
- The Student can resubmit the report.
- The Faculty Coordinator can approve the resubmitted report.
- The system updates compliance status appropriately following the workflow.

### 9.2 Priority

This is the first major implementation milestone and should be completed and verified before significant effort is spent on secondary modules.

The milestone must demonstrate the end-to-end behavior rather than isolated screens or disconnected features.

These outcomes define the milestone at a product level. Detailed validation rules, interface behavior, edge cases, and technical implementation will be specified in subsequent requirements and design documents.

## 10. High-Level Functional Expectations

Each essential module must satisfy the following product-level expectations:

- **Authentication and RBAC:** Enforce role-based access control and appropriate permissions.
- **User Management:** Support user account and role administration.
- **Internship Management:** Track internship details and lifecycle state.
- **Cohort Management:** Support cohort configuration within the initial deployment scope.
- **Document Management:** Support secure document submission, controlled access, and authorized review.
- **Weekly Reports and Work Logs:** Support submission, correction cycles, resubmission, and approval. Work logs take priority over attendance.
- **Faculty Review Workflow:** Provide structured feedback, review decisions, and approval tracking.
- **Compliance Engine:** Track mandatory requirements and update compliance status.
- **Dashboards:** Provide role-specific views of relevant information and actions.
- **Notifications:** Support email and in-app notifications for relevant workflow events.
- **Completion Verification:** Require satisfied mandatory requirements and authorized approval before recording completion.
- **Audit History:** Record and expose relevant historical events to authorized users.
- **Reports and CSV Exports:** Provide basic operational reports and permission-controlled CSV exports.

Detailed functional rules, validation criteria, permissions, state transitions, and edge cases will be specified in the FRD. Software-level functional and non-functional requirements will be detailed in the SRS.

## 11. Established Product and Technical Decisions

The following decisions establish the current product and technical baseline and must be preserved unless explicitly reviewed and changed.

### 11.1 Product and Workflow Decisions

| Area | Established decision |
|---|---|
| Initial deployment | One college department or internship cohort |
| Weekly report approval | Faculty Coordinators approve weekly reports |
| Work logs | Prioritized over attendance tracking |
| Attendance | Optional for the initial MVP |
| Company Mentor functionality | Optional for the initial MVP |
| First major milestone | Report submission, correction request, resubmission, approval, and compliance-status update |
| Development timeline | Planned duration of 12 weeks |
| Completion | Mandatory requirements must be satisfied and authorized approval granted |
| Audit history | Important actions and decisions must be preserved |

### 11.2 Technical Decisions

| Area | Established decision |
|---|---|
| Frontend | Next.js, React, and TypeScript |
| Backend | Java and Spring Boot |
| Database | PostgreSQL |
| Architecture | Modular monolith |
| Authentication and authorization | Spring Security |
| ORM and persistence | Spring Data JPA and Hibernate |
| Styling | Tailwind CSS |
| File storage | Private object storage |
| Notifications | Email and in-app notifications |
| Testing tools | JUnit, Spring Boot Test, and Playwright |
| Frontend deployment | Vercel |
| Backend deployment | Suitable backend hosting, to be selected and configured |

These decisions define the current baseline. Detailed implementation choices and unresolved requirements are documented in Section 18 and must be addressed at the appropriate stage.

## 12. Non-Functional Expectations

The following expectations guide technical planning. Where a measurable target or policy has not been established, it remains an open decision rather than an invented commitment.

| Aspect | Expectation | Status and unresolved details |
|---|---|---|
| Security and privacy | Apply role-based access control, secure authentication, private file storage, and appropriate protection of sensitive data. | Core security approach established. Encryption standards, data classification, and detailed privacy policies remain open. |
| Reliability | Core workflows should function reliably within the demonstration scope. | Demonstration-focused expectation. No formal uptime target or SLA has been established. |
| Usability and accessibility | Provide understandable, responsive interfaces for the supported roles. | Next.js and Tailwind CSS are established technologies. Target accessibility conformance level remains open. |
| Performance | Provide acceptable response times for the initial department or cohort. | No numerical response-time, throughput, or load targets have been established. |
| Maintainability | Use a modular monolith with clear module boundaries and consistent development conventions. | Architecture is established. Quantitative code-quality targets remain open. |
| Data integrity | Maintain consistency across internship state, compliance status, reports, and audit history. | Core business expectations are established. Detailed constraints and validations require further specification. |
| Backup and recovery | Protect data through an appropriate backup and recovery strategy. | Backup frequency, retention, and recovery targets remain open. |
| Auditability | Preserve important actions and decisions for traceability. | General requirement established. The precise event list and retention policy remain open. |

Measurable non-functional targets must be established in the SRS where practical. These should include appropriate performance targets, security verification requirements, and measurable testing expectations. Targets must reflect the intended deployment and available resources rather than arbitrary numerical commitments.

## 13. Success Metrics

MVP success will first be assessed through demonstrable technical acceptance. Real-world adoption targets will not be invented before user validation.

### 13.1 Technical Acceptance Criteria

- [ ] **First major workflow:** The complete report submission, correction, resubmission, approval, and compliance-update flow works end to end.
- [ ] **Role permissions:** Access control correctly enforces permissions for Students, Faculty Coordinators, and College Administrators.
- [ ] **Compliance accuracy:** Compliance status reflects the relevant workflow actions and mandatory requirement state.
- [ ] **Document workflow:** Document submission and authorized review work as specified.
- [ ] **Completion verification:** Completion is prevented until mandatory requirements are satisfied and authorized approval is granted.
- [ ] **Audit history:** Important actions and decisions are recorded and accessible to authorized users.
- [ ] **Reports and CSV exports:** Defined MVP reports and permission-controlled CSV exports function correctly.
- [ ] **Automated testing:** Tests covering critical workflows pass using the selected testing stack.
- [ ] **Deployment and demonstration:** The application is deployed and usable for the intended demonstration.

These are acceptance targets, not claims that implementation or testing has already been completed.

### 13.2 Real-World Adoption Metrics

User adoption, usage, satisfaction, and other quantitative targets have not yet been established.

After a demonstration and initial user feedback, appropriate measures and baselines can be defined. Until then, numerical adoption targets remain open decisions.

## 14. Constraints, Dependencies, Risks, and Assumptions

### 14.1 Constraints

- **Timeline:** Planned development duration of 12 weeks.
- **Initial deployment scope:** One college department or internship cohort.
- **Architecture:** Modular monolith rather than microservices.
- **Technology stack:** Next.js, React, TypeScript, Tailwind CSS, Java, Spring Boot, PostgreSQL, Spring Security, and Spring Data JPA/Hibernate.
- **Feature priority:** Work logs take priority over attendance; attendance and Company Mentor functionality are optional for the initial MVP.
- **Release focus:** Complete the first major workflow before concentrating on secondary modules.

### 14.2 Dependencies

- **Database:** A configured PostgreSQL instance.
- **File storage:** Available private object storage and appropriate access controls.
- **Email delivery:** A configured email delivery service.
- **In-app notifications:** Application functionality for displaying relevant notifications.
- **Hosting infrastructure:** Vercel for the frontend and suitable backend hosting.
- **Testing infrastructure:** Access to the selected test frameworks and environments needed to exercise critical workflows.

The availability and configuration of these dependencies must be verified during implementation.

### 14.3 Risks

- **Scope creep:** Optional features could expand the project beyond the planned timeline.
- **Inconsistent requirements:** Conflicting definitions across requirements and technical documents could produce implementation errors.
- **Timeline pressure:** The 12-week schedule may be difficult to maintain if scope grows or integration work takes longer than expected.
- **Integration complexity:** File storage, email delivery, database configuration, and deployment may introduce delays.
- **Unresolved requirements:** Decisions about retention, security policies, deadlines, and detailed completion criteria could affect later design or implementation.
- **Testing gaps:** Inadequate coverage of role permissions and workflow transitions could allow defects into the demonstration.

### 14.4 Assumptions

The following are planning assumptions, not confirmed external commitments:

- A suitable demonstration environment can be configured within the planned timeline.
- Representative demonstration data can be prepared.
- The single-department or cohort scope will keep the initial implementation manageable.
- Faculty review scenarios can be demonstrated without assuming a real faculty member has committed to participate.
- Required development tools and hosting services will be accessible.
- Optional functionality can be kept outside the MVP unless a deliberate scope decision changes the plan.

These assumptions should be validated when they become relevant to implementation or delivery.

## 15. MVP Release Criteria

The MVP release is evaluated against the following priorities.

### 15.1 Priority 1 — First Major Workflow

The report submission, correction request, resubmission, approval, and compliance-update workflow must function end to end.

This workflow must be implemented and verified before significant effort is spent on secondary modules.

### 15.2 Priority 2 — Remaining Essential MVP Modules

After the first major workflow is verified, the remaining essential modules must be implemented sufficiently to support the agreed MVP scope.

All 13 modules listed in Section 7 remain in the planned MVP. Prioritization establishes implementation order; it does not remove modules from the release scope.

### 15.3 Priority 3 — Security, Integrity, and Auditability

- Role-based access control must enforce the defined permissions.
- Compliance and completion logic must follow the established business rules.
- Important actions and decisions must be recorded in audit history.
- Reports and CSV exports must respect user permissions.
- Critical workflow tests must pass.

### 15.4 Priority 4 — Deployment and Demonstration

- The frontend and backend must be deployed to their selected hosting environments and work together.
- Representative demonstration data must be available to exercise the core workflows.
- The essential workflows must be demonstrable without relying on optional post-MVP functionality.

The MVP is ready for demonstration when the applicable criteria above have been satisfied. This section describes the required release state and does not imply that any criterion has already been met.

## 16. Future Enhancements

The following features may be considered after the initial MVP:

- **Company Mentor evaluations:** Structured evaluations and related mentor workflows.
- **Attendance tracking:** Attendance management, while retaining work logs as the primary priority.
- **Advanced analytics and PDF reports:** Additional reporting and export capabilities.
- **Additional notification channels:** SMS, push notifications, or other channels beyond email and in-app notifications.
- **Multi-department or multi-college support:** Expansion beyond the initial deployment scope.
- **External system integrations:** Integration with college systems such as learning management or student information systems.

These enhancements are outside the initial MVP scope and require separate prioritization before implementation.

## 17. Glossary

| Term | Definition |
|---|---|
| Cohort | A group of students or internships managed together, such as by department, program, or internship period. |
| Work Log | A record of work performed by a student during the internship. Work logs take priority over attendance tracking. |
| Weekly Report | A structured report submitted by a student for Faculty Coordinator review and approval. |
| Compliance | The state of satisfying the mandatory requirements defined for an internship or cohort. |
| Faculty Coordinator | The faculty member responsible for reviewing submissions, requesting corrections, approving reports, and verifying completion. |
| Completion Verification | The process of confirming internship completion after mandatory requirements are satisfied and authorized approval is granted. |
| Audit History | A record of important actions and decisions maintained for traceability. |
| Correction Request | A request from a Faculty Coordinator for a student to revise and resubmit a report or other submission. |
| Modular Monolith | A single deployable application organized into modules with defined responsibilities and boundaries. |
| MVP | Minimum Viable Product: the initial product scope needed to demonstrate core value and the first major milestone. |
| RBAC | Role-Based Access Control: a method of restricting access and actions according to assigned user roles. |
| PRD | Product Requirements Document: a document describing the product's vision, goals, scope, users, and high-level requirements. |
| FRD | Functional Requirements Document: a document describing the system's functional behavior and requirements in greater detail. |
| SRS | Software Requirements Specification: a document specifying software requirements, including functional and non-functional requirements. |

## 18. Open Decisions

The following details remain unresolved. They must be addressed at the appropriate planning or implementation stage. Decisions that could materially change architecture, database structure, access control, or core business workflows must be resolved before the affected design is finalized.

### 18.1 Decisions Required Before Architecture and Database Design Are Finalized

- **Requirement configuration and ownership:** Determine which requirements are configurable at cohort level, which users can create or change them, and whether requirement changes affect existing internships.
- **Completion and compliance rules:** Define the data and rules needed to represent mandatory requirements, determine compliance status, and establish completion eligibility.
- **Report lifecycle and modification rules:** Define report states and transitions, including whether and under what circumstances an approved report may be modified, reopened, or superseded.
- **Role permissions and administrative authority:** Establish which roles can manage users, assign roles, modify requirements, review submissions, and verify completion.
- **Audit history model:** Determine which actions must be recorded and what information must be preserved to support traceability.
- **Record relationships and lifecycle:** Define the relationships between students, internships, cohorts, reports, documents, requirements, reviews, and completion records.

These decisions affect the data model, module boundaries, authorization design, or core workflow behavior. They must be addressed before the corresponding architecture and database decisions are finalized.

### 18.2 Product and Process Decisions

- **Record retention:** Determine how long completed records and their history must be retained and under what rules.
- **Audit event coverage:** Finalize the specific events recorded in audit history and their required details.
- **Document requirements:** Define the required document types and validation rules for each applicable cohort.
- **Report cadence and deadlines:** Define submission schedules, deadlines, grace periods, and handling of late submissions.
- **Company Mentor capabilities:** If introduced after the MVP, define mentor permissions, workflows, and data-access boundaries.

### 18.3 Non-Functional Targets

- **Performance:** Establish response-time, throughput, and load targets appropriate for the intended deployment.
- **Availability:** Determine whether an uptime target or formal service-level agreement is required.
- **Accessibility:** Select the target accessibility conformance level.
- **Security and privacy:** Define encryption requirements, data classification, personal-data handling, and applicable institutional policies.
- **Backup and recovery:** Establish backup frequency, retention duration, and recovery time and recovery point objectives.
- **Code quality and testing:** Define measurable expectations for test coverage, critical-path coverage, and other relevant quality checks where practical.

These targets will be documented in the SRS and supporting technical documents before they are treated as acceptance commitments.

### 18.4 Adoption Metrics

- **Success baselines:** Establish appropriate adoption, usage, and satisfaction metrics after initial demonstrations or user feedback.

Quantitative adoption targets have not been established and must not be fabricated.