## Frontend Overview (Angular)

The frontend is a lightweight interface built with **Angular** to demonstrate the capabilities of the backend API.

### Features & Implementation

* **Role-Based Interface**: The application features a functional login and illustrates the distinct views and actions available to each security role (Manager, Coworker, Employee).
* **User Management Views**: Includes pages for listing and viewing employee profiles.
* **Development**: The initial boilerplate was quickly scaffolded using **GitHub Copilot** to focus development efforts on backend service implementation and API integration.

-----

### Future Improvements
* **Design and Responsiveness**: Perform a complete overhaul of the CSS and design to modernize the interface. Implement Responsive Web Design techniques to ensure optimal user experience across all devices (desktop, tablet, mobile).

* **State Management**: Introduce a robust state management pattern (e.g., NgRx or a centralized service layer) for coherent management of application state (authentication data, resource lists).

* **Internationalization (i18n)**: Add multi-language support to make the application scalable for international HR contexts.

## 3\. Quick Start Guide

### 3.1. Prerequisites

* Java Development Kit (JDK) 17 or later
* Apache Maven
* Node.js and npm (LTS recommended for Angular)
* Angular CLI
* **Docker** (required for the backend's Testcontainers to start MongoDB locally)


### 3.2. Frontend Startup

1.  Navigate to the `frontend` directory.

    ```bash
    cd frontend
    ```

2.  Install project dependencies.

    ```bash
    npm install
    ```

3.  Start the Angular development server.

    ```bash
    ng serve --open
    ```

    The application will open automatically in your default browser, typically at `http://localhost:4200`. Use the credentials from the **Default Application Users** table to log in.
