FROM mysql:8.0

ENV MYSQL_ROOT_PASSWORD=example
ENV MYSQL_DATABASE=employees

# Install curl and download the test database (Oracle Linux uses microdnf)
RUN microdnf install -y curl && \
    cd /docker-entrypoint-initdb.d && \
    curl -L -O https://github.com/datacharmer/test_db/raw/master/employees.sql && \
    curl -L -O https://github.com/datacharmer/test_db/raw/master/load_departments.dump && \
    curl -L -O https://github.com/datacharmer/test_db/raw/master/load_employees.dump && \
    curl -L -O https://github.com/datacharmer/test_db/raw/master/load_dept_emp.dump && \
    curl -L -O https://github.com/datacharmer/test_db/raw/master/load_dept_manager.dump && \
    curl -L -O https://github.com/datacharmer/test_db/raw/master/load_titles.dump && \
    curl -L -O https://github.com/datacharmer/test_db/raw/master/load_salaries1.dump && \
    curl -L -O https://github.com/datacharmer/test_db/raw/master/load_salaries2.dump && \
    curl -L -O https://github.com/datacharmer/test_db/raw/master/load_salaries3.dump && \
    curl -L -O https://github.com/datacharmer/test_db/raw/master/show_elapsed.sql && \
    microdnf clean all