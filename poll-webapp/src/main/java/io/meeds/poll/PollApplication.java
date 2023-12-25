/*
 * This file is part of the Meeds project (https://meeds.io/).
 * Copyright (C) 2020 - 2022 Meeds Association contact@meeds.io
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package io.meeds.poll;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.meeds.kernel.test.KernelExtension;
import io.meeds.spring.AvailableIntegration;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;

@ExtendWith({ SpringExtension.class, KernelExtension.class })
@SpringBootApplication(scanBasePackages = {
  PollApplication.MODULE_NAME,
  AvailableIntegration.KERNEL_MODULE,
  AvailableIntegration.JPA_MODULE,
  AvailableIntegration.LIQUIBASE_MODULE,
  AvailableIntegration.WEB_SECURITY_MODULE,
  AvailableIntegration.WEB_TRANSACTION_MODULE,
})
@EnableJpaRepositories(basePackages = PollApplication.MODULE_NAME)
@TestPropertySource(properties = {
  "spring.liquibase.change-log=" + PollApplication.CHANGELOG_PATH,
})
public class PollApplication extends SpringBootServletInitializer {

  public static final String MODULE_NAME    = "io.meeds.poll";

  public static final String CHANGELOG_PATH = "classpath:db/changelog/poll-rdbms.db.changelog.xml";

  @Override
  public void onStartup(ServletContext servletContext) throws ServletException {
    // Used to disable LogBack initialization in WebApp context after having
    // initialized it already in Meeds Server globally
    System.setProperty("org.springframework.boot.logging.LoggingSystem", "none");
    super.onStartup(servletContext);
  }

}
