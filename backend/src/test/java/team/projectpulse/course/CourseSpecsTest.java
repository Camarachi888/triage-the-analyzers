package team.projectpulse.course;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@ResourceLock(Resources.LOCALE)
class CourseSpecsTest {

    @Mock
    Root<Course> root;
    @Mock
    CriteriaQuery<?> query;
    @Mock
    CriteriaBuilder criteriaBuilder;
    @Mock
    Path<String> courseName;
    @Mock
    Expression<String> lowerCourseName;

    @Test
    void containsCourseNameUsesLocaleIndependentPattern() {
        Locale previousLocale = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            given(this.root.<String>get("courseName")).willReturn(this.courseName);
            given(this.criteriaBuilder.lower(this.courseName)).willReturn(this.lowerCourseName);

            CourseSpecs.containsCourseName("CITE").toPredicate(this.root, this.query, this.criteriaBuilder);

            ArgumentCaptor<String> pattern = ArgumentCaptor.forClass(String.class);
            verify(this.criteriaBuilder).like(eq(this.lowerCourseName), pattern.capture());
            assertThat(pattern.getValue()).isEqualTo("%cite%");
        } finally {
            Locale.setDefault(previousLocale);
        }
    }
}
