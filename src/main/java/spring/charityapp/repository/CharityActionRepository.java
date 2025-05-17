package spring.charityapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import spring.charityapp.model.CharityAction;
import spring.charityapp.common.ActionState;
import spring.charityapp.common.Category;

import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

@Repository
public interface CharityActionRepository extends JpaRepository<CharityAction, Integer>, JpaSpecificationExecutor<CharityAction>  {

    // Find all by category
    List<CharityAction> findByCategory(Category category);

    List<CharityAction> findTop6ByOrderByStartDateDesc();

    // Find all by action state
    List<CharityAction> findByActionState(ActionState actionState);

    // Find all by organization ID
    List<CharityAction> findByOrganization_Id(Integer organizationId);

    // Find actions between specific dates
    List<CharityAction> findByStartDateAfterAndEndDateBefore(Date start, Date end);

    // Search by title containing keyword
    List<CharityAction> findByTitleContainingIgnoreCase(String keyword);

    // Actions currently active
    List<CharityAction> findByStartDateBeforeAndEndDateAfter(Date now1, Date now2);

    // Find all sorted by collected amount (for popular actions)
    List<CharityAction> findTop5ByOrderByCollectedAmountDesc();
}
