package lms.step1.Repository;

import lms.step1.Model.SystemSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SystemSettingRepository extends JpaRepository<SystemSetting, Long> {
    boolean existsByKey(String key);
}
