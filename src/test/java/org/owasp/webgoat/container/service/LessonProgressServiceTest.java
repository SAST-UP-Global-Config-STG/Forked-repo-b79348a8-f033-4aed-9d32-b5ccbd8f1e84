package org.owasp.webgoat.container.service;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.assertj.core.util.Maps;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.owasp.webgoat.container.lessons.Assignment;
import org.owasp.webgoat.container.lessons.Lesson;
import org.owasp.webgoat.container.session.Course;
import org.owasp.webgoat.container.users.AssignmentProgress;
import org.owasp.webgoat.container.users.LessonProgress;
import org.owasp.webgoat.container.users.UserProgress;
import org.owasp.webgoat.container.users.UserProgressRepository;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.org.checkerframework.checker.units.qual.A;

/**
 * ************************************************************************************************
 * This file is part of WebGoat, an Open Web Application Security Project utility. For details,
 * please see http://www.owasp.org/
 *
 * <p>Copyright (c) 2002 - 2014 Bruce Mayhew
 *
 * <p>This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * <p>This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * <p>You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * <p>Getting Source ==============
 *
 * <p>Source for this application is maintained at https://github.com/WebGoat/WebGoat, a repository
 * for free software projects.
 *
 * <p>
 *
 * @author nbaars
 * @version $Id: $Id
 * @since November 25, 2016
 */
@ExtendWith(MockitoExtension.class)
class LessonProgressServiceTest {

  private MockMvc mockMvc;

  @Mock private Lesson lesson;
  @Mock private UserProgress userProgress;
  @Mock private LessonProgress lessonTracker;
  @Mock private UserProgressRepository userProgressRepository;
  @Mock private Course course;

  @BeforeEach
  void setup() {
    Assignment assignment = new Assignment("test", "test", List.of());
    AssignmentProgress assignmentProgress = new AssignmentProgress(assignment);
    when(userProgressRepository.findByUser(any())).thenReturn(userProgress);
    when(userProgress.getLessonProgress(any(Lesson.class))).thenReturn(lessonTracker);
    when(course.getLessonByName(any())).thenReturn(lesson);
    when(lessonTracker.getLessonOverview()).thenReturn(Maps.newHashMap(assignmentProgress, true));
    this.mockMvc =
        MockMvcBuilders.standaloneSetup(new LessonProgressService(userProgressRepository, course))
            .build();
  }

  @Test
  void jsonLessonOverview() throws Exception {
    this.mockMvc
        .perform(
            MockMvcRequestBuilders.get("/service/lessonoverview.mvc/test.lesson")
                .accept(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].assignment.name", is("test")))
        .andExpect(jsonPath("$[0].solved", is(true)));
  }
}
