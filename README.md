## Welcome to group 5's project: Board Game Hub.
This application is a social network for all board game lovers. Players and game owners can unite here to connect, share their games and organize events. Below are deliverable descriptions and tables listing the team members, roles and task breakdown per deliverable. Feel free to browse the repository [wiki](https://github.com/McGill-ECSE321-Winter2025/Group-5/wiki) for more information on the project.\
\
Here is a short introduction to the members of Group 5 who are working on this project:

**Marc Abou Abdallah**\
Marc is a third-year software engineering student with an interest in software development and AI. His personal hobbies include soccer, going to the gym, and working out.

**Deon Aftahi**\
Deon is a second-year Software Engineering student with a strong interest in full-stack development and entrepreneurship. He enjoys building seamless and interactive applications from the ground up. Outside of academics, he spends his time working out, traveling, and playing guitar.

**Marleine Atalla**\
Marleine is a third year Computer Engineering student. Some of her interests include Robotics, Automation, and Artificial Intelligence. Beyond the classroom setting, she enjoys running and traveling. 

**Jerome Courtemanche**\
Jerome is a second year computer engineering student. His interest in computers and software started at a young age with video games. Curiosity made his passion evolve to processors and other chips. Although he is mainly interested in working with hardware, he enjoys writing software as well.

**Bora Denizasan**\
Bora is a second year computer engineering student. From an early age, robots piqued his interest. Similarly, robotics is his area of interest. In his spare time, He enjoys playing overlooked video games to relax.

**Mubeen Mohammed**\
Mubeen is a third year software engineering student. His interest in AI applications and machine learning. In his free time, he enjoys playing chess and building cool stuff and automating them

**Raphaël Verger**\
Raphaël is a third year computer engineering student. He is mainly focused on machine learning, data analysis and software development. Outside of the classroom, he enjoys traveling and taking pictures!
***
### Deliverable 3
The scope of this deliverable includes the whole implementation of the frontend of our application. We started by creating a plan using a Miro board, followed up by creating the design using vue and the necessary typescript scripts, and finished by connecting our UI to the controller layer of our backend. More information information about design decisions can be found in the [project report](https://github.com/McGill-ECSE321-Winter2025/Group-5/wiki/Project-Report-3) and [meeting minutes](https://github.com/McGill-ECSE321-Winter2025/Group-5/wiki) in the [wiki](https://github.com/McGill-ECSE321-Winter2025/Group-5/wiki) of the project.\
\
**Overview table:**
|Team member|Role|Tasks|Time spent|
| --- | --- | --- | --- |
|Marc Abou Abdallah||||
| Deon Aftahi ||||
|Jerome Courtemanche||||
|Bora Denizasan||||
|Mubeen Mohammed|Developer|Desgined and implemented the whole UI for the application also connected some components to the backend like login and signup. Reviewed teammates PR|~24h|
|Raphaël Verger|Developer|Implemented 'Events' page and 'My Events' table in the homepage - connecting it to the backend. Created DataSeeder and contributed to project documentation.  |~20h|
***
### Deliverable 2
The scope of this deliverable includes completing service & controller layers with a RESTful API, documenting and testing them thoroughly. We implemented all the required service and controller classes as well as the required DTO's and Exception classes. These were then tested thoroughly through unit and integration tests using JUnit5. Please visit the deliverable 2 section of the [wiki](https://github.com/McGill-ECSE321-Winter2025/Group-5/wiki) for all project documentation and design decisions. Information on project setup and installation can be found on the [Build System](https://github.com/McGill-ECSE321-Winter2025/Group-5/wiki/Build-System) page of the wiki. Information regarding team member roles, individual tasks and time allocation breakdown can be found in the table below.\
\
**Overview table:**
|Team member|Role|Tasks|Time spent|
| --- | --- | --- | --- |
|Marc Abou Abdallah| Developer, QA Tester | Implemented Event service and controller including unit and integration tests, Assisted other members in refining services and writing additional tests, Provided code reviews and fixed common bugs, Contributed to the wiki (QA report, design decisions, API summary)  |~24h|
| Deon Aftahi | Developer | Implemented the GameManagementService and GameController classes,  their respective integration and unit tests, and DTOs. |~19h|
|Marleine Atalla| Developer |Implemented the BorrowingService and BorrowRequestController along with their respective unit and integration tests and contributed to project documentation.|~19h|
|Jerome Courtemanche| Developer, QA Engineer, Documenter |Implemented: ReviewService & Controller and applicable unit and integration tests, required DTO's and exception classes. Reviewed and added to other member's testing classes. Created issue backlog at the start of sprint. Documented QA plan and build system. Added the new dependencies and gradle tasks. |~24h|
|Bora Denizasan|Developer|Implemented the RegistrationController and services about registration under the EventService along with their respective unit and integration tests. Contributed to project documentation.|~20h|
|Mubeen Mohammed|Developer|Implemented the PlayerService and PlayerController along with their respective unit and integration test. Reviewed other teammates pull requests and contributed to project documentation|~19h|
|Raphaël Verger|Developer|Implemented the "PersonalCollectionService" and "GameCopyController" along with their respective tests; and contributed to project documentation.|~21h|
***
### Deliverable 1
The scope of this deliverable included modeling and database setup for the semester project. First, we created a [requirements model](https://github.com/McGill-ECSE321-Winter2025/Group-5/wiki/Requirements) for the application, defining 12 functional and 3 non-functional requirements. Next, we worked on use cases, providing two [use case diagrams](https://github.com/McGill-ECSE321-Winter2025/Group-5/wiki/Use-Case-Diagrams) and a detailed description of [7 key use cases](https://github.com/McGill-ECSE321-Winter2025/Group-5/wiki/Use-Cases) from our diagrams.
The second part of the project was focused on the [exploratory domain model](https://github.com/McGill-ECSE321-Winter2025/Group-5/wiki/Class-Diagram), where we created a class diagram to represent the interactions in the application.
In the third phase of the deliverable, we implemented the model classes along with the persistence layer. We also conducted thorough [testing of our persistence layer](https://github.com/McGill-ECSE321-Winter2025/Group-5/wiki/Testing-Documentation) using JUnit.
Throughout the entire process, we carefully applied and documented our software engineering design process. Whether through [meeting minutes](https://github.com/McGill-ECSE321-Winter2025/Group-5/wiki/Meeting-minutes) or [design decisions](https://github.com/McGill-ECSE321-Winter2025/Group-5/wiki/Project-Report), every step of our journey is recorded in the [wiki](https://github.com/McGill-ECSE321-Winter2025/Group-5/wiki). 
Information regarding team roles and task distribution can be found in the table below.

[Project report](https://github.com/McGill-ECSE321-Winter2025/Group-5/wiki/Project-Report)\
\
**Overview table:**
Note: Some tasks are missing from the table because they were done by everyone in collaboration during team meetings, including:
* Requirements
* Use case diagrams
* Domain model
* Backlog maintenance

Coding and documentation tasks were split as outlined in the table below:

|Team member|Role|Tasks|Time spent|
| --- | --- | --- | --- |
|Marc Abou Abdallah|Developer, QA tester|"Requesting to Borrow a Game" use case specification, Implementing the "Event" feature (model, persistence, tests), Writing additional tests for other classes (BorrowRequest, Game, GameCopy), Assisting in documentation (wiki, key decisions, reviewing deliverable 0.5), Suggesting and providing iterations for the domain model.|~15hrs|
|Deon Aftahi|Developer| "Withdrawing from an event" use case specification. Implementing the "Game" class, persistence, and tests. Implementing the "GameCopy" class, persistence, and tests |~11hrs|
|Marleine Atalla|Developer|Writing of Functional and Non-Functional Requirements, "Creating an event" use case specification, Implementing the "BorrowRequest" class, persistence, and tests. Review of the Domain Model for design issues. Collaborated on writing/editing the Wiki.|~12hrs|
|Jerome Courtemanche|Written report director, developer|"Handling borrow requests" use case specification, setting up the repository, implementing the "Review" class with persistence and tests, documenting the README.md & organizing and writing the wiki, collaboration on final cleanup of project before submission, ensuring proper GitHub workflow within team, monitoring issues and PR's.|~15hrs|
|Bora Denizasan|Developer|"View lending history" use case specification, Implementing the "Registration" class, persistence and tests. Involved in the creation of the requirements model and domain model.|~11hrs|
|Mubeen Mohammed|Scrum Master, Developer|"Registering for an event" use case specification, Creating the GitHub project and issues, implementing the "Person" model, persistence and tests. Involved in key decisions for the domain model|~12hrs|
|Raphaël Verger|Developer, documentation specialist|"Adding a game to personal collection" use case specification, Providing initial version of the model domain, implementing the "BorrowStatus" class, describing key design decisions and writing testing documentation in the wiki.|~12hrs|
