package com.cms.auth.strategy;

import com.cms.common.enums.AuthProviderType;
import com.cms.common.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * ============================================================
 * STRATEGY CONTEXT
 * ============================================================
 * AuthStrategyContext tự động thu thập tất cả AuthStrategy beans
 * được đăng ký trong Spring context và map theo AuthProviderType.
 *
 * Cách hoạt động:
 *   1. Spring inject tất cả beans implement AuthStrategy
 *   2. Context tạo Map<AuthProviderType, AuthStrategy>
 *   3. Khi cần, context.getStrategy(type) trả về đúng implementation
 *
 * Để thêm strategy mới (ví dụ Facebook):
 *   - Tạo class FacebookAuthStrategy implements AuthStrategy
 *   - Annotate @Component
 *   - Context tự động nhận diện, không cần sửa code!
 * ============================================================
 */
@Slf4j
@Component
public class AuthStrategyContext {

    private final Map<AuthProviderType, AuthStrategy> strategyMap;

    /**
     * Spring tự inject List<AuthStrategy> chứa tất cả implementations
     */
    public AuthStrategyContext(List<AuthStrategy> strategies) {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(
                        AuthStrategy::getType,
                        Function.identity()
                ));

        log.info("AuthStrategyContext initialized with {} strategies: {}",
                strategies.size(),
                strategyMap.keySet()
        );
    }

    /**
     * Lấy AuthStrategy theo loại
     * @param type Loại strategy (LOCAL, GOOGLE, FACEBOOK)
     * @return Implementation tương ứng
     * @throws AppException nếu không tìm thấy strategy
     */
    public AuthStrategy getStrategy(AuthProviderType type) {
        AuthStrategy strategy = strategyMap.get(type);
        if (strategy == null) {
            throw AppException.badRequest(
                    "Unsupported authentication strategy: " + type
            );
        }
        return strategy;
    }

    /**
     * Kiểm tra strategy có tồn tại không
     */
    public boolean hasStrategy(AuthProviderType type) {
        return strategyMap.containsKey(type);
    }
}
