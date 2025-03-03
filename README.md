# PharmaApp

PharmaApp is a Java-based application aimed at managing pharmaceutical data and processes. This project was developed as a practical example of database and application design in the pharmaceutical domain.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Installation](#installation)
- [Usage](#usage)
- [Project Structure](#project-structure)
- [Contributing](#contributing)
- [License](#license)
- [Contact](#contact)

## Overview

PharmaApp is designed to streamline the management of pharmaceutical information. Whether you are looking to keep track of medication inventories, manage prescription data, or interface with a backend database, this application offers a framework that you can further extend.

## Features

- **Medication Management:** Track and update medication details.
- **Prescription Tracking:** Record and manage prescription data.
- **Database Integration:** Built to interact with a database for persistent storage.
- **Modular Design:** Easy-to-extend architecture for additional functionalities.
- *(Add more features as necessary.)*

## Installation

### Prerequisites

- **Java JDK:** Ensure you have Java 8 or higher installed.
- **IDE:** The project is set up as an IntelliJ IDEA module (TemaBD.iml). You can also use other Java IDEs if preferred.
- **Git:** To clone the repository.

### Steps

1. **Clone the Repository:**

   ```bash
   git clone https://github.com/bogdan624/PharmaApp.git
   cd PharmaApp
   ```

2. **Open in your IDE:**

   - If using IntelliJ IDEA, open the `TemaBD.iml` project file.
   - Alternatively, import the project as a Maven/Gradle project if you set it up that way.

3. **Build the Project:**

   - Use your IDE's build tools or compile using the command line:
     
     ```bash
     javac -d out src/**/*.java
     ```

## Usage

After successfully building the project, run the main class from your IDE or via the command line. For example:

```bash
java -cp out com.yourpackage.MainClass
```

*(Replace `com.yourpackage.MainClass` with the actual package and class name that contains the main method.)*

## Project Structure

- **src:** Contains the source code.
- **lib:** Contains external libraries or dependencies.
- **out/production/TemaBD:** Contains the compiled application files.
- **TemaBD.iml:** IntelliJ IDEA project configuration file.

## Contributing

Contributions are welcome! If you find a bug or have a feature request, please open an issue. You can also fork the repository and submit a pull request with your changes.

## Contact

For any questions or suggestions, feel free to create an issue on the repository or contact the maintainer directly.
