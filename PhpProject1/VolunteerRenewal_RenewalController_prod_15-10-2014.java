public  class VolunteerRenewal_RenewalController {

    public String isWantToBeAnAdultMember { get; set; }
    public String isInterestedInSearchingForOtherRole { get; set; }
    
    public Boolean displayWantToBeAnAdultMember { get; set; }
    public Boolean displayInterestedSearchingOtherRole { get; set; }
    public Boolean isUserRenewable { get; set;}
    public Boolean hasPaymentUrl { get; set;}
    public List<VolunteerPositionRenewalWrapper> volunteerPositionRenewalList { get; set; }

    public Set<String> campaignIdSet;
    public Set<String> campaignMemberIdSet;
    public Set<String> oldOpportunityIdSet;
    public Set<String> campaignMemberOpportunityIdSet;
    Set<String> oldMembershipOpportunityIdSet;

    public List<CampaignMember> campaignMemberList;

    public Map<String, String> campaignMemberIdVsCampaignName;
    public Map<String, String> campaignMemberCampaignNameVsId;
    public Map<String, String> campaignMemberIdVsParentCampaignName;
    public Map<String, String> campaignMemberIdVsParticipationType; 
    public Map<String, Date> campaignMemberOpportunityVrEndDateMap;

    public Contact loggedInContact;
    public String councilId;

    private List<Contact> contactList;

    public VolunteerRenewal_RenewalController() {
        volunteerPositionRenewalList = new List<VolunteerPositionRenewalWrapper>();
        campaignMemberList = new List<CampaignMember>();
        loggedInContact = new Contact();

        displayWantToBeAnAdultMember = false;
        displayInterestedSearchingOtherRole = true;
        isUserRenewable = false;
        Integer counti = 1;
        hasPaymentUrl = false;
        campaignIdSet = new Set<String>();
        campaignMemberIdSet = new Set<String>();
        oldOpportunityIdSet = new Set<String>();
        campaignMemberOpportunityIdSet = new Set<String>();
    }

    public pagereference displayCampaignMember(){

        volunteerPositionRenewalList = new List<VolunteerPositionRenewalWrapper>();
        campaignMemberList = new List<CampaignMember>();
        loggedInContact = new Contact();

        displayWantToBeAnAdultMember = false;
        displayInterestedSearchingOtherRole = true;
        isUserRenewable = false;
        Integer counti = 1;

        campaignIdSet = new Set<String>();
        campaignMemberIdSet = new Set<String>();
        oldOpportunityIdSet = new Set<String>();
        campaignMemberOpportunityIdSet = new Set<String>();

        User user = getCurrentUser();
        system.debug('user==>'+user);

        system.debug('usercontact ==>'+user.contactId);

        //if(user != null && user.Id != NULL)
            //VolunteerRenewalUtility.manualShareRead(user.Id);

        if(user.Id != NULL && user.contactId != NULL)
            contactList = VolunteerRenewalUtility.getContactList(user.contactId);

        if(contactList != null && contactList.size() > 0)
            loggedInContact = contactList[0];

        system.debug('contactList==>'+contactList);

        List<Zip_Code__c> zipCodeList = [
            Select Id
                 , Council__c
              From Zip_Code__c z 
             Where Zip_Code_Unique__c =: loggedInContact.MailingPostalCode 
        ];

        if(zipCodeList != null && zipCodeList.size() > 0)
            councilId = zipCodeList[0].Council__c;

        List<Opportunity> oldOpportunityList = new List<Opportunity>();

        system.debug('****** contactList size'+contactList.size()); 
        system.debug('contactList[0].CampaignMembers==>'+contactList[0].CampaignMembers);
        List<CampaignMember> campaignMemberRelatedWithContactList = new List<CampaignMember>();
        campaignMemberRelatedWithContactList = contactList[0].CampaignMembers;
        system.debug('campaignMemberRelatedWithContactList==>'+campaignMemberRelatedWithContactList);

        if(campaignMemberRelatedWithContactList.size() == 0) {
            system.debug('campaignMemberRelatedWithContactList==>'+campaignMemberRelatedWithContactList.size());
            ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.WARNING,'You are not eligible to renew role'));
        }

        if(contactList != null && contactList.size() > 0 && campaignMemberRelatedWithContactList != null && campaignMemberRelatedWithContactList.size() > 0) {
            isUserRenewable = true;

            campaignMemberIdVsCampaignName = new Map<String, String>();
            campaignMemberCampaignNameVsId = new Map<String, String>();
            campaignMemberIdVsParentCampaignName = new Map<String, String>();
            campaignMemberIdVsParticipationType = new Map<String, String>();

            system.debug('contactList ===: ' + contactList.size());
            if(contactList != null && contactList.size() > 0) {
                for(Contact contact : contactList) {
                    campaignMemberList = contact.CampaignMembers;

                    system.debug('campaignMemberList==>'+campaignMemberList.size());
                    if(campaignMemberList != null && campaignMemberList.size() > 0) {
                        for(CampaignMember campaignMember : campaignMemberList) {
                            if(campaignMember != null) {
                                counti++;
                                system.debug('campaignMember ==>' + campaignMember.Id + ' :==: ' + campaignMember);
                                system.debug('campaignMember.Membership__c ==>' + campaignMember.Membership__c);

                                if(campaignMember != null && campaignMember.CampaignId != null)
                                    campaignIdSet.add(campaignMember.CampaignId);
                                if(campaignMember != null && campaignMember.Id != null) {
                                    system.debug('====1===: ' + campaignMember.Id);
                                    campaignMemberIdSet.add(campaignMember.Id);
                                    system.debug('====2===: ' + campaignMemberIdSet);
                                }
                                try {
                                    if(campaignMember != null && campaignMember.Membership__c != null) {
                                        system.debug('===1=campaignMember.Membership__c==: ' + campaignMember.Membership__c);
                                        String membershipId = string.valueOf(campaignMember.Membership__c) != null ? string.valueOf(campaignMember.Membership__c) : '';
                                        system.debug('===membershipId==: ' + membershipId);
                                        if(membershipId != null) {
                                            if(membershipId.length() >= 15)
                                                campaignMemberOpportunityIdSet.add(campaignMember.Membership__c);
                                        }
                                    }
                                } catch (Exception Ex) {
                                    system.debug('===: ' + Ex.getMessage());
                                }
                                //campaignMemberOpportunityVrEndDateMap.put(campaignMember.Membership__c,campaignMember.End_Date__c);
                            }
                        }
                    }
                }
            }
            system.debug('campaignMemberOpportunityIdSet==>'+counti+'  '+campaignMemberOpportunityIdSet);
            system.debug('campaignMemberIdSet==>'+campaignMemberIdSet);
            if(!campaignMemberIdSet.isEmpty()) {
                oldOpportunityList = [
                    Select Id
                         , (Select Id
                                  , Membership__c 
                               From Campaign_Members__r 
                              where Id IN : campaignMemberIdSet ) 
                      From Opportunity
                     where Contact__c = :loggedInContact.Id
                ];
                system.debug('oldOpportunityList==>'+oldOpportunityList);
                if(oldOpportunityList != null && oldOpportunityList.size() > 0) {
                    for(Opportunity opportunity : oldOpportunityList) {
                        oldOpportunityIdSet.add(opportunity.Id);
                    }
                }
            }

            system.debug('oldOpportunityIdSet==>'+oldOpportunityIdSet);
            system.debug('campaignIdSet==>'+campaignIdSet);

            if(!campaignIdSet.isEmpty()) {
                List<Campaign> campaignList = VolunteerRenewalUtility.getCampaignList(campaignIdSet);
                system.debug('campaignList==>'+campaignList);
                if(campaignList != null && campaignList.size() > 0) {
                    for(Campaign campaign : campaignList) {
                        campaignMemberIdVsCampaignName.put(campaign.Id, campaign.Name);
                        campaignMemberCampaignNameVsId.put(campaign.Name, campaign.Id);
                        campaignMemberIdVsParentCampaignName.put(campaign.Id, campaign.Parent.Name);
                        campaignMemberIdVsParticipationType.put(campaign.Id, campaign.Participation__c);
                    }
                    system.debug('campaignMemberIdVsCampaignName==>'+campaignMemberIdVsCampaignName + 'campaignMemberIdVsParentCampaignName====>'+campaignMemberIdVsParentCampaignName);
                }
            }

            for(Contact contact : contactList) {
                campaignMemberList = contact.CampaignMembers;
                system.debug('campaignMemberList ==: ' + campaignMemberList.size());
                if(campaignMemberList.size() > 0)
                    for(CampaignMember campaignMember : campaignMemberList) {
                        Boolean showAllContinuePositionOptions = false;
                        if(campaignMember.Campaign.Participation__c == 'Troop' && campaignMember.Campaign.Job_Code_Category__c == 'Direct Service' && campaignMember.Campaign.Job_Code_Sub_Category__c == 'Primary')
                            showAllContinuePositionOptions = true;
                        if(!campaignMemberIdVsCampaignName.isEmpty() && campaignMemberIdVsCampaignName.ContainsKey(campaignMember.CampaignId) 
                                && !campaignMemberIdVsParentCampaignName.isEmpty() && campaignMemberIdVsParentCampaignName.ContainsKey(campaignMember.CampaignId))
                            volunteerPositionRenewalList.add(new VolunteerPositionRenewalWrapper(contact.Name , campaignMemberIdVsCampaignName.get(campaignMember.CampaignId), campaignMemberIdVsParentCampaignName.get(campaignMember.CampaignId), 'Yes', campaignMemberIdVsParticipationType.get(campaignMember.CampaignId), showAllContinuePositionOptions,campaignMember.Pending_Payment_URL__c));
                        if(campaignMember.Pending_Payment_URL__c !='' && campaignMember.Pending_Payment_URL__c !=null)
                        hasPaymentUrl = true; 
                    }
            }
            system.debug('volunteerPositionRenewalList==>'+volunteerPositionRenewalList);
        }
        return null;
    }

    public PageReference renewPosition() {
        boolean originalValue;
        rC_Event__CampaignMember_Setting__c objCampaignMemberSetting = rC_Event__CampaignMember_Setting__c.getInstance();
        objCampaignMemberSetting = (objCampaignMemberSetting != null) ? objCampaignMemberSetting : new rC_Event__CampaignMember_Setting__c();
        originalValue = objCampaignMemberSetting.rC_Event__Disable_All__c;
        objCampaignMemberSetting.rC_Event__Disable_All__c = true;
        upsert objCampaignMemberSetting;

        Set<String> rolesToBeRenewCampaignIdSet = new Set<String>();
        Set<String> rolesToBeNotRenewCampaignIdSet = new Set<String>();
        oldMembershipOpportunityIdSet = new Set<String>();
        Set<String> campaignMemberIdSet = new Set<String>();
        Opportunity oldOpportunity = new Opportunity();

        List<CampaignMember> updateCampaignMemberList = new List<CampaignMember>();
        List<CampaignMember> nonRenewedCampaignMemberList = new List<CampaignMember>();
        List<CampaignMember> renewedCampaignMemberList = new List<CampaignMember>();

        String campaignMemberIds = '';

        Map<Id, Date> opportunityIdVrProductEndDateMap = new Map<Id, Date>();
        Map<Id, String> renewCampaignMemberIdVrcontinuePositionMap = new Map<Id, String>();
        Map<Id, String> nonRenewCampaignMemberIdVrcontinuePositionMap = new Map<Id, String>();

        for(VolunteerPositionRenewalWrapper wrapper : volunteerPositionRenewalList) {
            system.debug('rin for ==>'+wrapper.continuePosition);
            if(wrapper.continuePosition.trim().toUppercase().contains('YES')) {
                system.debug('renewedCampaignMemberListYES==>');
                if(wrapper.paymentUrl != '' && wrapper.paymentUrl !=null) {
                string urlPayment = wrapper.paymentUrl.substring(4); 
                PageReference page = new PageReference(urlPayment);                
                page.setRedirect(false);
                return page;
                }
                rolesToBeRenewCampaignIdSet.add(campaignMemberCampaignNameVsId.get(wrapper.positon));
                renewCampaignMemberIdVrcontinuePositionMap.put(campaignMemberCampaignNameVsId.get(wrapper.positon), wrapper.continuePosition);
            }
            else if(wrapper.continuePosition.trim().toUppercase().contains('NO')) {
                system.debug('renewedCampaignMemberListNO==>');
                rolesToBeNotRenewCampaignIdSet.add(campaignMemberCampaignNameVsId.get(wrapper.positon));
                nonRenewCampaignMemberIdVrcontinuePositionMap.put(campaignMemberCampaignNameVsId.get(wrapper.positon), wrapper.continuePosition);
            }
        }

        system.debug('renew map==>'+renewCampaignMemberIdVrcontinuePositionMap);
        system.debug('non renew map==>'+nonRenewCampaignMemberIdVrcontinuePositionMap);

        if(rolesToBeRenewCampaignIdSet != null) {

            renewedCampaignMemberList = VolunteerRenewalUtility.getCampaignMembers(rolesToBeRenewCampaignIdSet, loggedInContact);

            /*renewedCampaignMemberList = [
                Select Id
                     , CampaignId
                     , Active__c
                     , End_Date__c
                     , Display_Renewal__c
                     , Membership__c
                  from CampaignMember
                 Where CampaignId IN :rolesToBeRenewCampaignIdSet
                   and ContactId = :loggedInContact.Id
            ];*/
            system.debug('renewedCampaignMemberList==>'+renewedCampaignMemberList);
            if(renewedCampaignMemberList != null && renewedCampaignMemberList.size() > 0) {
                for(CampaignMember campaignMember : renewedCampaignMemberList) {
                    if(campaignMember.Id != null)
                        campaignMemberIdSet.add(campaignMember.Id);
                    if(campaignMember.Membership__c != null){
                        system.debug('old Membership Opportunitys==>'+campaignMember.Membership__c);
                        oldMembershipOpportunityIdSet.add(campaignMember.Membership__c);
                    }
                    system.debug('continue id==>'+campaignMember.CampaignId);
                    system.debug('continue==>'+renewCampaignMemberIdVrcontinuePositionMap.get(campaignMember.CampaignId));
                    campaignMember.Continue_This_Position__c = renewCampaignMemberIdVrcontinuePositionMap.get(campaignMember.CampaignId);
                    campaignMember.Renewal_Date__c = Date.today();
                    
                    system.debug('Date.today()###########'+Date.today() + 'campaignMember.Renewal_Date__c#####'+campaignMember.Renewal_Date__c);
                    
                    updateCampaignMemberList.add(campaignMember);
                }
            }
        }
        system.debug('old Membership Opportunitys==>'+oldMembershipOpportunityIdSet);
        oldOpportunity = getOldOpportunity();
        system.debug('old Membership Opportunity==>'+oldOpportunity);

        system.debug('rolesToBeRenewCampaignIdSet==>'+rolesToBeRenewCampaignIdSet);
        system.debug('rolesToBeNotRenewCampaignIdSet==>'+rolesToBeNotRenewCampaignIdSet);

        if(rolesToBeNotRenewCampaignIdSet != null) {

            nonRenewedCampaignMemberList = VolunteerRenewalUtility.getCampaignMembers(rolesToBeNotRenewCampaignIdSet, loggedInContact);

            /*nonRenewedCampaignMemberList = [
                Select Id
                     , CampaignId
                     , Active__c
                     , End_Date__c
                     , Display_Renewal__c
                     , Membership__c
                  from CampaignMember
                 Where CampaignId IN :rolesToBeNotRenewCampaignIdSet
                   and ContactId = :loggedInContact.Id
            ];*/

            system.debug('nonRenewedCampaignMemberList==>'+nonRenewedCampaignMemberList);

            List<OpportunityLineItem> opportunityLineItemList = [
                Select PricebookEntry.Product2Id
                     , PricebookEntry.Product2.rC_Giving__End_Date__c
                     , PricebookEntry.Name
                     , PricebookEntryId
                     , OpportunityId 
                  From OpportunityLineItem
                 Where OpportunityId IN : campaignMemberOpportunityIdSet
            ];

            system.debug('opportunityLineItemList==>'+opportunityLineItemList);

            if(opportunityLineItemList != null && opportunityLineItemList.size() > 0) {

                for(OpportunityLineItem opportunityLineItem : opportunityLineItemList) {
                    opportunityIdVrProductEndDateMap.put(opportunityLineItem.OpportunityId, opportunityLineItem.PricebookEntry.Product2.rC_Giving__End_Date__c);
                }
            }
            system.debug('opportunityIdVrProductEndDateMap==>'+opportunityIdVrProductEndDateMap);
            if(nonRenewedCampaignMemberList != null && nonRenewedCampaignMemberList.size() > 0) {
                for(CampaignMember campaignMember : nonRenewedCampaignMemberList) {
                    //campaignMember.Active__c = false;
                    if(campaignMember.Membership__c != null)
                        campaignMember.End_Date__c = opportunityIdVrProductEndDateMap.get(campaignMember.Membership__c);

                    campaignMember.Display_Renewal__c = false;
                    system.debug('continue==>'+nonRenewCampaignMemberIdVrcontinuePositionMap.get(campaignMember.CampaignId));
                    campaignMember.Continue_This_Position__c = nonRenewCampaignMemberIdVrcontinuePositionMap.get(campaignMember.CampaignId);
                    updateCampaignMemberList.add(campaignMember);
                }
            }
            system.debug('updateCampaignMemberList==>'+updateCampaignMemberList);

            if(updateCampaignMemberList != null && updateCampaignMemberList.size() > 0) {
                /*try {
                    update updateCampaignMemberList;
                    system.debug('updateCampaignMemberList==>'+updateCampaignMemberList);
                } catch(Exception Ex) {
                    system.debug('== updateCampaignMemberList Exception :====>  ' + ex.getMessage());
                }*/

                VolunteerRenewalUtility.updateCampaignMemberList(updateCampaignMemberList);
            }
        }

        if(campaignMemberIdSet.size() > 0) {
            for(Id campaignMemberId : campaignMemberIdSet) {
                campaignMemberIds = campaignMemberIds == '' ?  string.valueOf(campaignMemberId) : campaignMemberIds + ',' + string.valueOf(campaignMemberId);
            }
        }

        //Scenario 7 
        if(isWantToBeAnAdultMember != null && isWantToBeAnAdultMember !='' && isWantToBeAnAdultMember.equalsIgnoreCase('No')) {
            system.debug('***isWantToBeAnAdultMemberInside***');
            PageReference volRenewalThankYou = new PageReference('/apex/VolunteerRenewal_ThankYou');
            volRenewalThankYou.getParameters().put('ContactId', loggedInContact.Id);
            volRenewalThankYou.getParameters().put('CampaignMemberIds', campaignMemberIds);
            if(councilId != null && councilId != '')
                volRenewalThankYou.getParameters().put('CouncilId', councilId);
            if(oldOpportunity != null)
                volRenewalThankYou.getParameters().put('OldOpportunityId',oldOpportunity.Id);
            volRenewalThankYou.setRedirect(true);
            return volRenewalThankYou;
        }
        //if(rolesToBeNotRenewCampaignIdSet != null) {
            //Scenario 1,2,5
            if(isInterestedInSearchingForOtherRole.equalsIgnoreCase('Yes')) {

                PageReference volRenewalTroopSearch = new PageReference('/apex/VolunteerRenewal_TroopGroupRoleSearch');
                volRenewalTroopSearch.getParameters().put('ContactId', loggedInContact.Id);
                volRenewalTroopSearch.getParameters().put('CampaignMemberIds', campaignMemberIds);
                if(councilId != null && councilId != '')
                    volRenewalTroopSearch.getParameters().put('CouncilId', councilId);
                if(oldOpportunity != null)
                    volRenewalTroopSearch.getParameters().put('OldOpportunityId',oldOpportunity.Id);
                volRenewalTroopSearch.setRedirect(true);
                return volRenewalTroopSearch;
            }

            //Scenario 3,4,6
            if(isInterestedInSearchingForOtherRole.equalsIgnoreCase('No')) {

                PageReference volRenewalMembershipInfo = new PageReference('/apex/VolunteerRenewal_MembershipInformation');
                volRenewalMembershipInfo.getParameters().put('ContactId', loggedInContact.Id);
                volRenewalMembershipInfo.getParameters().put('CampaignMemberIds', campaignMemberIds);
                if(councilId != null && councilId != '')
                    volRenewalMembershipInfo.getParameters().put('CouncilId', councilId);
                system.debug('volRenewalMembershipInfo===>'+volRenewalMembershipInfo);
                volRenewalMembershipInfo.setRedirect(true);
                return volRenewalMembershipInfo;
            }
            objCampaignMemberSetting.rC_Event__Disable_All__c = originalValue;
        update objCampaignMemberSetting;
        //} 
        return null;
    }

    public Opportunity getOldOpportunity(){
        List<Opportunity> opportunityList = new List<Opportunity>();
        opportunityList = [
            Select Id
                 , Background_Check__c
              From Opportunity
             Where Id IN : oldMembershipOpportunityIdSet
        ];
        system.debug('==opportunityList :  ' + opportunityList);
        return (opportunityList != null && opportunityList.size() > 0) ? opportunityList[0] : null;
    }

    public PageReference displayWantToBeAnAdultMember() {
        displayWantToBeAnAdultMember = false;
        system.debug('In ===>');
        system.debug('volunteerPositionRenewalList===>'+volunteerPositionRenewalList.size());
        Integer counterContinuePosition = 0;
        if(volunteerPositionRenewalList != null && volunteerPositionRenewalList.size() > 0) {
            for(VolunteerPositionRenewalWrapper wrapper : volunteerPositionRenewalList) {
                if(wrapper.continuePosition.Contains('No'))
                    counterContinuePosition++;
            }
            system.debug('counterContinuePosition===>'+counterContinuePosition);
            
            if(volunteerPositionRenewalList.size() == counterContinuePosition)
                displayWantToBeAnAdultMember = true;
        }
        system.debug('displayWantToBeAnAdultMember===>'+displayWantToBeAnAdultMember);
        return null;
    }

    public PageReference displayWantToSearchForOtherRoles() {
        displayInterestedSearchingOtherRole  = false;
        system.debug('In ===>');
        system.debug('volunteerPositionRenewalList===>'+volunteerPositionRenewalList.size());
        Integer counterContinuePosition = 0;
        if(volunteerPositionRenewalList != null && volunteerPositionRenewalList.size() > 0) {
            for(VolunteerPositionRenewalWrapper wrapper : volunteerPositionRenewalList) {
                if(wrapper.continuePosition.equalsIgnoreCase('Yes'))
                    counterContinuePosition++;
            }

            system.debug('counterContinuePosition===>'+counterContinuePosition);

            if(volunteerPositionRenewalList.size() == counterContinuePosition)
                displayInterestedSearchingOtherRole = true;
        }
        system.debug('displayWantToBeAnAdultMember===>'+displayWantToBeAnAdultMember);
        return null;

    }
    
    public PageReference hideWantToSearchForOtherRoles() {
        system.debug('===isWantToBeAnAdultMember ===: ' + isWantToBeAnAdultMember);
        displayInterestedSearchingOtherRole = (isWantToBeAnAdultMember.equalsIgnoreCase('Yes')) ? true : false;
        return null;
    }

    public User getCurrentUser() {
        List<User> userList = [
            Select Username
                 , UserRoleId
                 , Name
                 , Id
                 , Email
                 , Alias
                 , contactId 
              From User
             Where Id =: UserInfo.getUserId()
             limit 1
        ];
        system.debug('userList==>'+userList);
        
        User user = (userList != null && userList.size() > 0) ? userList[0] : new User();
        return user;
    }

    public List<SelectOption> getContinueThisPosition() {
        List<SelectOption> continueThisPosition = new List<SelectOption>();
        continueThisPosition.add(new SelectOption('Yes', 'Yes'));
        continueThisPosition.add(new SelectOption('No, I won\'t, but the troop will continue', 'No, I won\'t, but the troop will continue'));
        continueThisPosition.add(new SelectOption('No, Our troop is disbanding/graduating', 'No, Our troop is disbanding/graduating')); 
        continueThisPosition.add(new SelectOption('No, Not sure what the troop is plannig', 'No, Not sure what the troop is plannig'));
        return continueThisPosition;
    }

    public List<SelectOption> getContinueThisPositionYesOrNo() {
        List<SelectOption> continueThisPosition = new List<SelectOption>();
        continueThisPosition.add(new SelectOption('Yes', 'Yes'));
        continueThisPosition.add(new SelectOption('No', 'No'));
        return continueThisPosition;
    }

    public List<SelectOption> getWantToBeAnAdultMember() {
        List<SelectOption> wantToBeAnAdultMember = new List<SelectOption>();
        wantToBeAnAdultMember.add(new SelectOption('Yes', 'Yes'));
        wantToBeAnAdultMember.add(new SelectOption('No', 'No'));
        return wantToBeAnAdultMember;
    }

    public List<SelectOption> getInterestedInSearchingForOtherRole() {
        List<SelectOption> interestedInSearchingForOtherRole = new List<SelectOption>();
        interestedInSearchingForOtherRole.add(new SelectOption('Yes', 'Yes'));
        interestedInSearchingForOtherRole.add(new SelectOption('No', 'No'));
        return interestedInSearchingForOtherRole;
    }

    public class VolunteerPositionRenewalWrapper {
        public String contactName { get; set; }
        public String positon { get; set; }
        public String troopName { get; set; }
        public String continuePosition { get; set; }
        public String participationType { get; set; }
        public Boolean showAllContinuePositionOptions { get; set; }
        public String paymentUrl { get; set; }
        public VolunteerPositionRenewalWrapper(String strContactName, String strPositon, String strTroopName, String strContinueThisPosition, String strParticipationType, Boolean booleanShowAllContinuePositionOptions, String paymentUrl) {
            this.contactName = strContactName;
            this.positon = strPositon;
            this.troopName = strTroopName; 
            this.continuePosition = strContinueThisPosition;
            this.participationType = strParticipationType;
            this.showAllContinuePositionOptions = booleanShowAllContinuePositionOptions;
            this.paymentUrl = paymentUrl;
        }
    }

     public void manualShareRead(Id userOrGroupId){
        //Job__Share jobShr  = new Job__Share();
        CampaignShare campaignShare = new CampaignShare();

        //jobShr.ParentId = recordId;

        campaignShare.UserOrGroupId = userOrGroupId;

      // Set the access level.
      campaignShare.CampaignAccessLevel= 'Edit';

      Database.SaveResult sr = Database.insert(campaignShare);
      
      if(sr.isSuccess()){
         // Indicates success
         system.debug('==>');
      }
      else {
         // Get first save result error.
         Database.Error err = sr.getErrors()[0];
         system.debug('==>'+err);
         // Check if the error is related to trival access level.
         // Access levels equal or more permissive than the object's default 
         // access level are not allowed. 
         // These sharing records are not required and thus an insert exception is acceptable. 
         if(err.getStatusCode() == StatusCode.FIELD_FILTER_VALIDATION_EXCEPTION  &&  
                  err.getMessage().contains('AccessLevel')){
            // Indicates success.
            //return true;
         }
         else{
            // Indicates failure.
            //return false;
         }
       }
       
    }
}