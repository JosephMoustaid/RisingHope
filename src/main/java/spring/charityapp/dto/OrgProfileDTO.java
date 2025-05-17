package spring.charityapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrgProfileDTO {
    private OrganizationDTO organization;
    private long totalCharityActions;
    private long totalDonations;
    private double totalMoneyRaised;
    private int totalProgramsWithAchievedGoals;
    private List<CharityActionDTO> charityActions;
}