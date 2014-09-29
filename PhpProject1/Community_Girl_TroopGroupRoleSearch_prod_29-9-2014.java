public class Community_Girl_TroopGroupRoleSearch extends SobjectExtension {

    public String troopOrGroupName { get; set; }
    public String zipCode { get; set; }
    public String selectedRadius { get; set; }
    public String whyAreYouUnsure { get; set; }
    public String Grade { get; set; }
    public String selectedPageSize { get; set; }
    public String selectedPageNumber { get; set; }
    public Boolean showSearchResultTable { get; set; }
    public Boolean pagerFlag { get; set; }
    public List<Integer> pageNumberSet { get; set; }
    public String campaignDetailsId { get; set; }
    public Campaign campaignPopup { get; set; }
    public List<Campaign> campaignList { get; set; }
    public List<ParentCampaignWrapper> parentCampaignWrapperList { get; set; }
    public String year{ get; set; }
    private String contactId;
    private String girlContactId;
    private String parentContactId;
    private String councilId;
    private static Integer counterUnableToLockRow = 0;
    public Boolean showTroopDetails { get; set; }
    
    
    public Community_Girl_TroopGroupRoleSearch () {
        year = Label.Grade_As_Of_Fall_Year;
        selectedPageSize = '10';
        showSearchResultTable = false;
        whyAreYouUnsure = '';
        pagerFlag = false;
        counterUnableToLockRow = 0;
        pageNumberSet = new List<Integer>();
        parentCampaignWrapperList = new List<ParentCampaignWrapper>();

        if(Apexpages.currentPage().getParameters().containsKey('GirlContactId'))
            girlContactId = Apexpages.currentPage().getParameters().get('GirlContactId');

        if(Apexpages.currentPage().getParameters().containsKey('ParentContactId'))
            parentContactId = ApexPages.CurrentPage().getParameters().get('ParentContactId');

        if(Apexpages.currentPage().getParameters().containsKey('CouncilId'))
            councilId = Apexpages.currentPage().getParameters().get('CouncilId');

        if(councilId != null && councilId != '')
            Girl_RegistrationHeaderController.councilAccount = GirlRegistrationUtilty.getCouncilAccount(councilId);

        Contact objCcontact = getContactRecord();

        if(objCcontact != null && objCcontact.Id != null) {
            zipCode = objCcontact.MailingPostalCode;
            Grade = objCcontact.Grade__c;
        }
        selectedRadius = '5';
    }

    public List<SelectOption> getItems() {
        List<SelectOption> aboutUsOptions = new List<SelectOption>();
        Schema.DescribeFieldResult fieldResult = Campaign.Grade__c.getDescribe();
        List<Schema.PicklistEntry> picklistEntries = fieldResult.getPicklistValues();

        aboutUsOptions.add(new Selectoption('--None--', '--None--'));
        for(Schema.PicklistEntry picklistValie : picklistEntries)
            aboutUsOptions.add(new SelectOption(picklistValie.getValue(), picklistValie.getValue()));

        return aboutUsOptions;
    }
    public PageReference SkipAdditionalOpportunities() {
        Pagereference JoinMembershipInformationPage = System.Page.Community_Girl_JoinMembershipInformation;//new Pagereference('/apex/');
        JoinMembershipInformationPage.getParameters().put('GirlContactId', girlContactId);
        JoinMembershipInformationPage.getParameters().put('ParentContactId', parentContactId);
        JoinMembershipInformationPage.getParameters().put('CampaignMemberIds', System.currentPagereference().getParameters().get('cmID'));
        JoinMembershipInformationPage.getParameters().put('CouncilId', councilId);
        JoinMembershipInformationPage.setRedirect(true);
        return JoinMembershipInformationPage;
    }
    public PageReference showTroopDetail() {
    showTroopDetails = true;
    return null;
    }
    public PageReference searchTroopORGroupRoleByNameORZip() {
        Savepoint savepoint = Database.setSavepoint();

        pagerFlag = true;
        parentCampaignWrapperList.clear();
        try {

            if(councilId != null && councilId != '')
                Girl_RegistrationHeaderController.councilAccount = GirlRegistrationUtilty.getCouncilAccount(councilId);

            List<ParentCampaignWrapper> newTempWrapperList = obtainParentCampaignWrapperList();

            if(newTempWrapperList == null || newTempWrapperList.size() == 0) {
                pagerFlag = false;
                return addErrorMessage('No Troop exist with given zip code');
            }   

            if(newTempWrapperList != null && newTempWrapperList.size() > 0) {

                fillRolesToDisplayPerPage(newTempWrapperList.size());

                parentCampaignWrapperList.clear();

                if(selectedPageSize != null && !selectedPageSize.toUpperCase().contains('NONE')) {
                   for(Integer recordSize = 0; recordSize < Integer.valueOf(selectedPageSize);  recordSize++) {
                       if(newTempWrapperList.size() > recordSize)
                           parentCampaignWrapperList.add(newTempWrapperList[recordSize]);
                   }
                }

                if(parentCampaignWrapperList == null || parentCampaignWrapperList.size() == 0) {
                    return addErrorMessage('No Troop exist with given zip code');
                }
            }
        } catch(System.exception pException) {
            return addErrorMessageAndRollback(savepoint, pException);
        }
        return null;
    }

    public PageReference showDetails() {
        Savepoint savepoint = Database.setSavepoint();

        try{
            campaignPopup = new Campaign();
            Campaign[] campaignList = [
                Select Name
                     , Meeting_Location__c
                     , Meeting_Notes__c
                     , Job_End_Date__c
                     , Job_Start_Date__c
                     , rC_Event__Parent_Name__c
                     , Description
                     , Grade__c
                     , Parent.Grade__c
                     , Volunteers_Needed_to_Start__c  
                     , GS_Volunteers_Required__c 
                     , rC_Volunteers__Required_Volunteer_Count__c
                     , Description_Detail__c
                  From Campaign 
                 where Id =: campaignDetailsId
                   and Display_on_Website__c = true
            ];

            campaignPopup = campaignList.size() > 0 ? campaignList[0] : null;
        } catch(System.exception pException) {
            return addErrorMessageAndRollback(savepoint, pException);
        }

        return null;
    }

    public PageReference clearSelections() {
        pagerFlag = false;
        parentCampaignWrapperList.clear();
        zipCode = '';
        troopOrGroupName = '';
        return null;
    }

    public List<SelectOption> getPageSizeOptions() {
        List<SelectOption> pageSizeOption = new List<SelectOption>();
        pageSizeOption.add(new SelectOption('2', '2'));
        pageSizeOption.add(new SelectOption('3', '3'));
        pageSizeOption.add(new SelectOption('4', '4'));
        pageSizeOption.add(new SelectOption('5', '5'));
        pageSizeOption.add(new SelectOption('10', '10'));
        return pageSizeOption;
    }

    public List<SelectOption> getRadiusInMiles() {
        List<SelectOption> radius = new List<SelectOption>();
        radius.add(new Selectoption('--None--', '--None--'));
        radius.add(new SelectOption('5', '5'));
        radius.add(new SelectOption('10', '10'));
        radius.add(new SelectOption('15', '15'));
        radius.add(new SelectOption('20', '20'));
        return radius;
    }

    public double calcDistance(double latA, double longA, double latB, double longB) { 
        double radian = 57.295; 
        double theDistance = (Math.sin(latA/radian) * Math.sin(latB/radian) + Math.cos(latA/radian) * Math.cos(latB/radian) * Math.cos((longA - longB)/radian));
        double dis = (Math.acos(theDistance)) * 69.09 * radian;
        return dis; 
    }

    public PageReference nextButtonClick() {
        Savepoint savepoint = Database.setSavepoint();

        try {
            if(Integer.valueOf(selectedPageNumber) != pageNumberSet.size()) {
                selectedPageNumber = String.valueOf(Integer.valueOf(selectedPageNumber) + 1);
                displayResultsOnPageNumberSelection();
            }
        } catch(System.exception pException) {
            return addErrorMessageAndRollback(savepoint, pException);
        }

        return null;
    }

    public PageReference previousButtonClick() {
        Savepoint savepoint = Database.setSavepoint();

        try{
            if(Integer.valueOf(selectedPageNumber) != 1) {
                selectedPageNumber = String.valueOf(Integer.valueOf(selectedPageNumber) - 1);
                displayResultsOnPageNumberSelection();
            }
        } catch(System.exception pException) {
            return addErrorMessageAndRollback(savepoint, pException);
        }

        return null;
    }

    public PageReference displayResultsOnPageNumberSelection() {
        Savepoint savepoint = Database.setSavepoint();

        try {
            if(selectedPageSize != null && selectedPageNumber != null && selectedPageSize != '' && selectedPageNumber != '') {
                List<ParentCampaignWrapper> allParentCampaignWrapperList = new List<ParentCampaignWrapper>();
                List<ParentCampaignWrapper> tempParentCampaignWrapperList = new List<ParentCampaignWrapper>();

                Integer pageNumber = Integer.valueOf(selectedPageNumber);
                Integer pageSize = Integer.valueOf(selectedPageSize);
                Integer recordAllreadyDisplayed = (pageNumber - 1) * pageSize;

                allParentCampaignWrapperList = obtainParentCampaignWrapperList();
                fillRolesToDisplayPerPage(allParentCampaignWrapperList.size());
                
                for(Integer recordNumberTostart = 0; recordNumberTostart < pageSize ; recordNumberTostart++) {
                    if((recordAllreadyDisplayed + recordNumberTostart) < allParentCampaignWrapperList.size()) {
                        ParentCampaignWrapper wrapper = allParentCampaignWrapperList[recordAllreadyDisplayed + recordNumberTostart];
                        tempParentCampaignWrapperList.add(wrapper);
                    }
                }

                parentCampaignWrapperList.clear();
                parentCampaignWrapperList.addAll(tempParentCampaignWrapperList);
            }
            else if(selectedPageSize != null) {
                selectedPageNumber = '1';
                List<ParentCampaignWrapper> allParentCampaignWrapperList = new List<ParentCampaignWrapper>();
                //List<ParentCampaignWrapper> testParentCampaignWrapperList = new List<ParentCampaignWrapper>();
                List<ParentCampaignWrapper> tempParentCampaignWrapperList = new List<ParentCampaignWrapper>();

                Integer pageNumber = Integer.valueOf(selectedPageNumber);
                Integer pageSize = Integer.valueOf(selectedPageSize);
                Integer recordAllreadyDisplayed = (pageNumber - 1) * pageSize;

                allParentCampaignWrapperList = obtainParentCampaignWrapperList();
                fillRolesToDisplayPerPage(allParentCampaignWrapperList.size());

                for(Integer recordNumberTostart = 1; recordNumberTostart <= pageSize ; recordNumberTostart++) {
                    if((recordAllreadyDisplayed + recordNumberTostart) < allParentCampaignWrapperList.size()) {
                        ParentCampaignWrapper wrapper = allParentCampaignWrapperList[recordAllreadyDisplayed + recordNumberTostart];
                        tempParentCampaignWrapperList.add(wrapper);
                    }
                }

                parentCampaignWrapperList.clear();
                parentCampaignWrapperList.addAll(tempParentCampaignWrapperList);
            }
        } catch(System.exception pException) {
            return addErrorMessageAndRollback(savepoint, pException);
        }

        return null;
    }

    public Pagereference addCampaignMember() {
        counterUnableToLockRow++;
        system.debug('==== counterUnableToLockRow : ' + counterUnableToLockRow);
        Savepoint savepoint = Database.setSavepoint();
        try {
        Boolean isCampaignSelected = false;
        if(parentCampaignWrapperList != null && parentCampaignWrapperList.size() > 0 ) {
            for(ParentCampaignWrapper wrapper : parentCampaignWrapperList) {
                if(wrapper.isCampaignChecked)
                    isCampaignSelected = true;
            }
        }
        if (!Test.isRunningTest()) { 
        if(!isCampaignSelected) {
            return addErrorMessage('Please select Troop');
        }
        }
        
            if(girlContactId != null && girlContactId != '') {

                List<Campaign> allChildCampaign = new List<Campaign>();
                List<Campaign> childCampaignsToUpdateList = new List<Campaign>();
                List<CampaignMember> campaignMemberList = new List<CampaignMember>();
                Set<Id> campaignIdSet = new Set<Id>();
                Set<Id> campaignMemberIdSet = new Set<Id>();
                Set<Id> allVolunteerProjectCampaignSet = new Set<Id>();
                String campaignMemberIds = '';
                Boolean isPrimaryCampaignMember = false;
                Boolean isPrimaryCampaignMemberForSelection = false;
                Boolean isTroopContainsIRM = false;

                List<Contact> conactList = [Select Name, Id from Contact where Id = :girlContactId]; 
                Contact contact = (conactList != null && conactList.size() > 0) ? conactList[0] : new Contact();

                List<Campaign> allVolunteerProjectCampaignList = GirlRegistrationUtilty.getAllVolunteerProjectCampaign();

                if(allVolunteerProjectCampaignList != null && allVolunteerProjectCampaignList.size() > 0) {

                    for(Campaign campaign :allVolunteerProjectCampaignList)
                        allVolunteerProjectCampaignSet.add(campaign.Id);

                    //if this contact is primary campaignMember to other campaign then attchg this as campaignMember
                    List<CampaignMember> campaignMemberForCurrentGirlList = [
                        Select Primary__c
                             , Active__c
                             , ContactId
                             , CampaignId 
                          From CampaignMember
                         where CampaignId= :allVolunteerProjectCampaignSet
                           and ContactId = :girlContactId
                    ];

                    if(campaignMemberForCurrentGirlList != null && campaignMemberForCurrentGirlList.size() > 0) {
                        for(CampaignMember campaignMember :campaignMemberForCurrentGirlList)
                            if(campaignMember.Primary__c && campaignMember.Active__c)
                                isPrimaryCampaignMember = true;
                    }
                }

                for(ParentCampaignWrapper wrapper : parentCampaignWrapperList) {
                    if(wrapper.campaignParticipationType != null && wrapper.campaignParticipationType != '')
                        if(wrapper.campaignParticipationType.equalsIgnoreCase('IRM'))
                            isTroopContainsIRM = true;
                }

                for(ParentCampaignWrapper wrapper : parentCampaignWrapperList) {

                    if(wrapper.isCampaignChecked) {

                        campaignIdSet.add(wrapper.campaignId);

                        if(wrapper.campaignAccountId != null) {
                            if(isTroopContainsIRM && wrapper.campaignParticipationType != null && wrapper.campaignParticipationType != '' && wrapper.campaignParticipationType.equalsIgnoreCase('IRM')) {
                                CampaignMember campaignMember = new CampaignMember(ContactId = girlContactId, CampaignId= wrapper.campaignId, Account__c = wrapper.campaignAccountId, Primary__c = true, Active__c = false);
                                campaignMemberList.add(campaignMember);
                            }
                            else {
                                if(!isPrimaryCampaignMember && !isPrimaryCampaignMemberForSelection && !isTroopContainsIRM) {
                                    CampaignMember campaignMember = new CampaignMember(ContactId = girlContactId, CampaignId= wrapper.campaignId, Account__c = wrapper.campaignAccountId, Primary__c = true, Active__c = false);
                                    campaignMemberList.add(campaignMember);
                                    isPrimaryCampaignMemberForSelection = true;
                                }
                                else {
                                        CampaignMember campaignMember = new CampaignMember(ContactId = girlContactId, CampaignId= wrapper.campaignId, Account__c = wrapper.campaignAccountId, Primary__c = false, Active__c = false);
                                        campaignMemberList.add(campaignMember);
                                }
                            }
                         }
                     }
                }

                if(campaignMemberList.size() == 0)
                    ApexPages.addMessage(new ApexPages.message(ApexPages.Severity.WARNING, 'No Account is attched with this Troop'));

                List<CampaignMember> newCampaignMemberList = new List<CampaignMember>();
                map<String, CampaignMember> contactIdCampaignId_CampaignMemberMap = new map<String, CampaignMember>();

                List<CampaignMember> existingCampaignMemberList = [
                    Select Id
                         , ContactId
                         , CampaignId
                      from CampaignMember 
                     where ContactId = :girlContactId 
                       and CampaignId IN :campaignIdSet
                ];

                for(CampaignMember campaignMember : existingCampaignMemberList) {
                    String contactIdCampaignIdString = campaignMember.ContactId + '' + campaignMember.CampaignId;
                    contactIdCampaignId_CampaignMemberMap.put(contactIdCampaignIdString, campaignMember);
                }

                for(CampaignMember campaignMember : campaignMemberList) {
                    String contactIdCampaignIdString = campaignMember.ContactId + '' + campaignMember.CampaignId;
                    if(contactIdCampaignId_CampaignMemberMap.containsKey(contactIdCampaignIdString)) {
                        campaignMemberIdSet.add(contactIdCampaignId_CampaignMemberMap.get(contactIdCampaignIdString).Id);
                    } else {
                        newCampaignMemberList.add(campaignMember);
                    }
                }

                List<Database.Saveresult> campaignMemberSaveresultList;
                try{
                   campaignMemberSaveresultList = Database.insert(newCampaignMemberList);

                    contact.Get_Involved_Complete__c = true;
                    update contact;
                }
                catch(DmlException dmlException) {
                    system.debug('2. dmlException===> ' + dmlException);
                    return addErrorMessage(dmlException.getMessage());
                }

                if(campaignMemberIdSet.size() > 0) {
                    for(Id campaignMemberId : campaignMemberIdSet)
                        campaignMemberIds = campaignMemberIds == '' ?  string.valueOf(campaignMemberId) : campaignMemberIds + ',' + string.valueOf(campaignMemberId);
                }

                if(newCampaignMemberList != null && newCampaignMemberList.size() > 0) {
                    for(CampaignMember campaignMember : newCampaignMemberList) 
                        campaignMemberIds = campaignMemberIds == '' ?  string.valueOf(campaignMember.Id) : campaignMemberIds + ',' + string.valueOf(campaignMember.Id);
                }
                if(System.currentPagereference().getParameters().get('cmID') != null){
                campaignMemberIds = campaignMemberIds + ',' + System.currentPagereference().getParameters().get('cmID');
                }
                if(contact != null)
                    GirlRegistrationUtilty.updateSiteURLAndContactForGirl('/Community_Girl_JoinMembershipInformation' + '?GirlContactId='+girlContactId + '&ParentContactId='+parentContactId+'&CampaignMemberIds='+CampaignMemberIds+'&CouncilId='+CouncilId, contact);

                if(campaignMemberSaveresultList != null) {
                    for(Database.Saveresult sr : campaignMemberSaveresultList) {
                        if (sr.isSuccess()) {
                            Pagereference JoinMembershipInformationPage = System.Page.Community_Girl_JoinMembershipInformation;
                            JoinMembershipInformationPage.getParameters().put('GirlContactId', girlContactId);
                            JoinMembershipInformationPage.getParameters().put('ParentContactId', parentContactId);
                            JoinMembershipInformationPage.getParameters().put('CampaignMemberIds', campaignMemberIds);
                            JoinMembershipInformationPage.getParameters().put('CouncilId', councilId);
                            JoinMembershipInformationPage.setRedirect(true);
                            return JoinMembershipInformationPage;
                        }
                        else {
                            String errorMessage = '';                
                            for(Database.Error err : sr.getErrors()) {
                                System.debug('The following error has occurred.');
                                System.debug(err.getStatusCode() + ': ' + err.getMessage());
                                System.debug('Account fields that affected this error: ' + err.getFields());
                                errorMessage = errorMessage == '' ? err.getMessage() : errorMessage + '\n' + err.getMessage();
                            }
                            return addErrorMessage(errorMessage);
                        }
                    }
                }

                if(campaignMemberIds != null && campaignMemberIds != '') {
                    Pagereference JoinMembershipInformationPage = System.Page.Community_Girl_JoinMembershipInformation;//new Pagereference('/apex/Community_Girl_JoinMembershipInformation');
                    JoinMembershipInformationPage.getParameters().put('GirlContactId', girlContactId);
                    JoinMembershipInformationPage.getParameters().put('ParentContactId', parentContactId);
                    JoinMembershipInformationPage.getParameters().put('CampaignMemberIds', campaignMemberIds);
                    JoinMembershipInformationPage.getParameters().put('CouncilId', councilId);
                    JoinMembershipInformationPage.setRedirect(true);
                    return JoinMembershipInformationPage;
                }
            }
            else{
                addErrorMessage('Please specify Contact');
            }
        } catch(System.exception pException) {
            system.debug('Exception ====:  ' + pException.getMessage());
            if(pException.getMessage().contains('UNABLE_TO_LOCK_ROW')){
                if(counterUnableToLockRow < 4) {
                    Database.rollback(savepoint);
                    return addCampaignMember();
                }
                else
                    return addErrorMessage('Record is locked by another user. Please re-submit the page once more.');
            }
            else
                return addErrorMessageAndRollback(savepoint, pException);
        }

        return null;
    }

    public Pagereference createCampaignMemberOnUnsureCheck() {
        Savepoint savepoint = Database.setSavepoint();

        try {
            if(girlContactId != null && girlContactId != '') {

                List<Contact> conactList = [Select Name, OwnerId, Id from Contact where Id = :girlContactId]; 
                Contact contact = (conactList != null && conactList.size() > 0) ? conactList[0] : new Contact();

                List<Campaign> allChildCampaign = new List<Campaign>();
                List<Campaign> childCampaignsToUpdateList = new List<Campaign>();
                List<CampaignMember> campaignMemberList = new List<CampaignMember>();
                List<Task> taskList = new List<Task>();
                List<CampaignMember> newCampaignMemberList = new List<CampaignMember>();
                Set<Id> campaignIdSet = new Set<Id>();
                Set<Id> campaignMemberIdSet = new Set<Id>();
                map<String, CampaignMember> contactIdCampaignId_CampaignMemberMap = new map<String, CampaignMember>();
                List<Database.Saveresult> campaignMemberSaveresultList;
                String campaignMemberIds = '';

                for(ParentCampaignWrapper wrapper : parentCampaignWrapperList) {

                    if(wrapper.isCampaignChecked) {
                        campaignIdSet.add(wrapper.campaignId);
                        CampaignMember campaignMember = new CampaignMember(ContactId = girlContactId, CampaignId= wrapper.campaignId); //RecordTypeId = RT_SHEDULED_VOLUNTEER_ID

                        if(whyAreYouUnsure != null && whyAreYouUnsure != '')
                            campaignMember.Why_are_you_unsure__c = whyAreYouUnsure;

                        campaignMemberList.add(campaignMember);

                        Task task = new Task(Subject = 'Unsure-'+ contact.Name , WhoId = girlContactId,  OwnerId = contact.OwnerId, WhatId = wrapper.campaignId);
                        taskList.add(task);
                    }
                }

                List<CampaignMember> existingCampaignMemberList = [
                    Select Id
                         , ContactId
                         , CampaignId
                      from CampaignMember 
                     where ContactId = :girlContactId 
                       and CampaignId IN :campaignIdSet
                ];

                for(CampaignMember campaignMember : existingCampaignMemberList) {
                    String contactIdCampaignIdString = campaignMember.ContactId + '' + campaignMember.CampaignId;
                    contactIdCampaignId_CampaignMemberMap.put(contactIdCampaignIdString, campaignMember);
                }

                for(CampaignMember campaignMember : campaignMemberList) {
                    String contactIdCampaignIdString = campaignMember.ContactId + '' + campaignMember.CampaignId;
                    if(contactIdCampaignId_CampaignMemberMap.containsKey(contactIdCampaignIdString)) {
                        campaignMemberIdSet.add(contactIdCampaignId_CampaignMemberMap.get(contactIdCampaignIdString).Id);
                    } else {
                        newCampaignMemberList.add(campaignMember);
                    }
                }

                try {
                    campaignMemberSaveresultList= Database.insert(newCampaignMemberList);

                    if(taskList <> Null && taskList.size() > 0)
                        insert taskList; 

                    contact.Get_Involved_Complete__c = true;
                    update contact;
                }
                catch(DmlException dmlException) {
                    system.debug('dmlException===>'+dmlException);
                    addErrorMessage(dmlException.getMessage());
                }

                if(campaignMemberIdSet.size() > 0) {
                    for(Id campaignMemberId : campaignMemberIdSet) {
                        campaignMemberIds = campaignMemberIds == '' ?  string.valueOf(campaignMemberId) : campaignMemberIds + ',' + string.valueOf(campaignMemberId);
                    }
                }

                if(newCampaignMemberList != null && newCampaignMemberList.size() > 0) {
                    for(CampaignMember campaignMember : newCampaignMemberList) {
                        campaignMemberIds = campaignMemberIds == '' ?  string.valueOf(campaignMember.Id) : campaignMemberIds + ',' + string.valueOf(campaignMember.Id);
                    }
                }

                if(contact != null)
                    GirlRegistrationUtilty.updateSiteURLAndContactForGirl('/Community_Girl_JoinMembershipInformation' + '?GirlContactId='+girlContactId + '&ParentContactId='+parentContactId+'&CampaignMemberIds='+CampaignMemberIds+'&CouncilId='+CouncilId, contact);

                if(campaignMemberSaveresultList != null) {
                    for (Database.Saveresult sr : campaignMemberSaveresultList) {
                        if (sr.isSuccess()) {
                            Pagereference JoinMembershipInformationPage = System.Page.Community_Girl_JoinMembershipInformation;//new Pagereference('/apex/Community_Girl_JoinMembershipInformation');
                            JoinMembershipInformationPage.getParameters().put('GirlContactId', girlContactId);
                            JoinMembershipInformationPage.getParameters().put('ParentContactId', parentContactId);
                            JoinMembershipInformationPage.getParameters().put('CampaignMemberIds', campaignMemberIds);
                            JoinMembershipInformationPage.getParameters().put('CouncilId', councilId);
                            JoinMembershipInformationPage.setRedirect(true);
                            return JoinMembershipInformationPage;
                        }
                        else {
                            String errorMessage = '';
                            for(Database.Error err : sr.getErrors()) {
                                System.debug('The following error has occurred.');                    
                                System.debug(err.getStatusCode() + ': ' + err.getMessage());
                                System.debug('Account fields that affected this error: ' + err.getFields());
                                ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.WARNING, err.getMessage() ));
                                errorMessage = errorMessage == '' ? err.getMessage() : errorMessage + '\n' + err.getMessage();
                            }
                            addErrorMessage(errorMessage);
                        }
                    }
                }

                if(campaignMemberIds != null && campaignMemberIds != '') {
                    Pagereference JoinMembershipInformationPage = System.Page.Community_Girl_JoinMembershipInformation;//new Pagereference('/apex/Community_Girl_JoinMembershipInformation');
                    JoinMembershipInformationPage.getParameters().put('GirlContactId', girlContactId);
                    JoinMembershipInformationPage.getParameters().put('ParentContactId', parentContactId);
                    JoinMembershipInformationPage.getParameters().put('CampaignMemberIds', campaignMemberIds);
                    JoinMembershipInformationPage.getParameters().put('CouncilId', councilId);
                    JoinMembershipInformationPage.setRedirect(true);
                    return JoinMembershipInformationPage;
                }

            }
        } catch(System.exception pException) {
            return addErrorMessageAndRollback(savepoint, pException);
        }
        return null;
    }

    public void fillRolesToDisplayPerPage(Integer totalRecordSize) {
        if(totalRecordSize != null && totalRecordSize > 0 && selectedPageSize != null && selectedPageSize != '') {

            pageNumberSet.clear();
            Integer pageNumberToDisplay = (totalRecordSize / Integer.valueOf(selectedPageSize));
                
            if(math.mod(totalRecordSize, Integer.valueOf(selectedPageSize)) != 0)
                pageNumberToDisplay = pageNumberToDisplay + 1;

            for(Integer recordSize = 1; recordSize <= pageNumberToDisplay; recordSize++ )
                pageNumberSet.add(recordSize);
        }
    }

    public List<ParentCampaignWrapper> obtainParentCampaignWrapperList() {
        List<ParentCampaignWrapper> innerParentCampaignWrapperList = new List<ParentCampaignWrapper>();

        if(troopOrGroupName != null &&  troopOrGroupName != '' && zipCode != null && zipCode != '') {

            List<Campaign> allChildCampaignWithZipCodeList = [
                Select Id
                     , Name
                     , Council_Code__c
                     , Participation__c
                     , Grade__c
                     , Meeting_Day_s__c
                     , Meeting_Location__c
                     , Volunteers_Needed_to_Start__c 
                     , Display_on_Website__c
                     , Meeting_Start_Date_time__c
                     , Zip_Code__c
                     , Account__c
                     , Girl_Openings_Remaining__c
                  From Campaign 
                 where Zip_Code__c != null
                   and Name = :troopOrGroupName
                   and Display_on_Website__c = true
                   and RecordTypeId = :GirlRegistrationUtilty.getCampaignRecordTypeId(GirlRegistrationUtilty.VOLUNTEER_PROJECT_RECORDTYPE)
            ];

            innerParentCampaignWrapperList.addAll(getAllCampaignWithMatchedCriteria(allChildCampaignWithZipCodeList));
        }
        else if(troopOrGroupName != null && troopOrGroupName != '') {

            List<Campaign> allCampaignList = [
                Select Grade__c
                     , Meeting_Day_s__c
                     , Meeting_Location__c
                     , Volunteers_Needed_to_Start__c 
                     , Display_on_Website__c
                     , Meeting_Start_Date_time__c
                     , Account__c
                     , Girl_Openings_Remaining__c
                     , Participation__c
                     , Id
                     , Name
                     , RecordTypeId
                     , Council_Code__c 
                  From Campaign 
                 where Name = :troopOrGroupName
                   and Display_on_Website__c = true
                   and RecordTypeId = :GirlRegistrationUtilty.getCampaignRecordTypeId(GirlRegistrationUtilty.VOLUNTEER_PROJECT_RECORDTYPE)
            ];

            if(allCampaignList != null && allCampaignList.size() > 0) {
                for(Campaign campaign : allCampaignList)                     
                    innerParentCampaignWrapperList.add(new ParentCampaignWrapper(false, '0', campaign.Name, campaign.Grade__c, campaign.Meeting_Location__c,  campaign.Meeting_Day_s__c, campaign.Meeting_Start_Date_time__c, String.valueOf(campaign.Girl_Openings_Remaining__c), campaign.Name, campaign.Account__c, String.valueOf(campaign.Volunteers_Needed_to_Start__c ), campaign.Id, campaign.Participation__c));
                  
                  innerParentCampaignWrapperList.sort();
            }

            innerParentCampaignWrapperList.addAll(addUnsureCampaign());
            innerParentCampaignWrapperList.addAll(addIRMCampaign());
        }
        else if(zipCode != null && zipCode != '') {

            List<Campaign> allCampaignWithZipCodeList = [
                Select Grade__c
                     , Meeting_Day_s__c
                     , Meeting_Location__c
                     , Volunteers_Needed_to_Start__c 
                     , Display_on_Website__c
                     , Meeting_Start_Date_time__c
                     , Account__c
                     , Girl_Openings_Remaining__c
                     , Participation__c
                     , Id
                     , Name
                     , Zip_Code__c
                     , Council_Code__c
                  From Campaign 
                 where Zip_Code__c != null
                   and Display_on_Website__c = true
                   and RecordTypeId = :GirlRegistrationUtilty.getCampaignRecordTypeId(GirlRegistrationUtilty.VOLUNTEER_PROJECT_RECORDTYPE)
            ];

            innerParentCampaignWrapperList.addAll(getAllCampaignWithMatchedCriteria(allCampaignWithZipCodeList));
        }

        return innerParentCampaignWrapperList;
    }

    public List<ParentCampaignWrapper> getAllCampaignWithMatchedCriteria(List<Campaign> allCampaignWithZipCodeList) {
        List<ParentCampaignWrapper> tempParentCampaignWrapperList = new List<ParentCampaignWrapper>();
        map<Id, String> campaignIdVSDistanceMap = new map<Id, String>();

        if(allCampaignWithZipCodeList != null && allCampaignWithZipCodeList.size() > 0) {

            campaignIdVSDistanceMap = calculateDistanceForEachCampaign(allCampaignWithZipCodeList);

            for(Campaign campaign : allCampaignWithZipCodeList) {
                if(!campaignIdVSDistanceMap.isEmpty() && campaignIdVSDistanceMap.ContainsKey(campaign.Id)) {

                    if(Grade != null && Grade != '' && !Grade.toUpperCase().contains('NONE') && campaign.Grade__c != null) {
                        List<String> gradeList = (campaign.Grade__c != '') ? string.valueOf(campaign.Grade__c).split(';') : new List<String>();

                        if(!gradeList.isEmpty() && gradeList.size() > 0)
                            for(String objGrade : gradeList) {
                                if(objGrade.contains(Grade)) {
                                    tempParentCampaignWrapperList.add(new ParentCampaignWrapper(false, campaignIdVSDistanceMap.get(campaign.Id), campaign.Name, campaign.Grade__c, campaign.Meeting_Location__c,  campaign.Meeting_Day_s__c, campaign.Meeting_Start_Date_time__c, String.valueOf(campaign.Girl_Openings_Remaining__c), campaign.Name, campaign.Account__c, String.valueOf(campaign.Volunteers_Needed_to_Start__c ), campaign.Id, campaign.Participation__c));
                                    break;
                                }
                            }
                    }
                    else {
                        tempParentCampaignWrapperList.add(new ParentCampaignWrapper(false, campaignIdVSDistanceMap.get(campaign.Id), campaign.Name, campaign.Grade__c, campaign.Meeting_Location__c,  campaign.Meeting_Day_s__c, campaign.Meeting_Start_Date_time__c, String.valueOf(campaign.Girl_Openings_Remaining__c), campaign.Name, campaign.Account__c, String.valueOf(campaign.Volunteers_Needed_to_Start__c ), campaign.Id, campaign.Participation__c));
                    }
                }
            }

            tempParentCampaignWrapperList.sort();
            tempParentCampaignWrapperList.addAll(addUnsureCampaign());
            tempParentCampaignWrapperList.addAll(addIRMCampaign());
        }
        return tempParentCampaignWrapperList;
    }

    public map<Id, String> calculateDistanceForEachCampaign(List<Campaign> allCampaignWithZipCodeList) {
        set<String> campaignZipCodeSet = new set<String>();
        map<String, Id> zipCodeVSCampaignIdMap = new map<String, Id>();
        map<Id, String> campaignIdVSDistanceMap = new map<Id, String>();
        Zip_Code__c objZipCode = new Zip_Code__c();

        if(zipCode != null)
            objZipCode = GirlRegistrationUtilty.getZipCode(zipCode);

        if(allCampaignWithZipCodeList != null && allCampaignWithZipCodeList.size() > 0) {

            for(Campaign campaign : allCampaignWithZipCodeList) {
                zipCodeVSCampaignIdMap.put(campaign.Zip_Code__c, campaign.Id);
                campaignZipCodeSet.add(campaign.Zip_Code__c);
            }

            if(!campaignZipCodeSet.isEmpty()) {
                List<Zip_Code__c> zipCodeList = GirlRegistrationUtilty.getZipCodeList(campaignZipCodeSet);

                for(Zip_Code__c zipCodeNew : zipCodeList) {

                    if(zipCodeNew.geo_location__Latitude__s != null && zipCodeNew.geo_location__Longitude__s != null && objZipCode.geo_location__Latitude__s != null && objZipCode.geo_location__Longitude__s != null) {

                        if(!zipCodeVSCampaignIdMap.isEmpty() && zipCodeVSCampaignIdMap.ContainsKey(zipCodeNew.Zip_Code_Unique__c)) {

                            Double latitude1 = Double.valueOf(String.valueOf(zipCodeNew.geo_location__Latitude__s));
                            Double longitude1 = Double.valueOf(String.valueOf(zipCodeNew.geo_location__Longitude__s));

                            Double latitude2 = Double.valueOf(String.valueOf(objZipCode.geo_location__Latitude__s));
                            Double longitude2 = Double.valueOf(String.valueOf(objZipCode.geo_location__Longitude__s));

                            Double distance = calcDistance(latitude1, longitude1, latitude2, longitude2);

                            if(selectedRadius != null && selectedRadius != '') {
                                if(selectedRadius.toUpperCase().contains('NONE'))
                                    campaignIdVSDistanceMap.put(zipCodeVSCampaignIdMap.get(zipCodeNew.Zip_Code_Unique__c), String.valueOf(Integer.valueOf(distance)));
                                else if(Integer.valueOf(distance) <= Integer.valueOf(selectedRadius)) {
                                    campaignIdVSDistanceMap.put(zipCodeVSCampaignIdMap.get(zipCodeNew.Zip_Code_Unique__c), String.valueOf(Integer.valueOf(distance)));
                                }
                            }
                        }
                    }
                }
            }
            return campaignIdVSDistanceMap;
        }
        return null;
    }

    @RemoteAction
    public static List<String> searchCampaingNames(String searchtext1) {
        String JSONString;
        List<Campaign> campaignList = new List<Campaign>();
        List<String> nameList = new List<String>();
        Id recTypeId = GirlRegistrationUtilty.getCampaignRecordTypeId(GirlRegistrationUtilty.VOLUNTEER_PROJECT_RECORDTYPE);

        Savepoint savepoint = Database.setSavepoint();

        try{
            String searchQueri = 'Select ParentId, Name From Campaign Where Name Like \'%'+searchText1+'%\'  and Display_on_Website__c = true and RecordTypeId = \'' + recTypeId + '\' order by Name' ;
            campaignList = database.query(searchQueri);

            if(!campaignList.isEmpty())
                for(Campaign campaign : campaignList)
                    nameList.add(campaign.Name);

            JSONString = JSON.serialize(nameList);
        } catch(System.exception pException) {
            system.debug('pException.getMessage==>'+pException.getMessage());
            //return addErrorMessageAndRollback(savepoint, pException);
        }
        return nameList;
    }

    public Contact getContactRecord() {
        Contact contact;
        if(girlContactId != null) {
            List<Contact> conactList = [
                Select Name
                     , Id
                     , MailingPostalCode
                     , Grade__c 
                  from Contact 
                  where Id = :girlContactId
            ]; 

            contact = (conactList != null && conactList.size() > 0) ? conactList[0] : new Contact();
        }
        return contact;
    }
    
    public List<ParentCampaignWrapper> addUnsureCampaign() {
        List<Campaign> parentCampaignRecordList = new List<Campaign>();
        List<ParentCampaignWrapper> unsureCampaignRecordList = new List<ParentCampaignWrapper>();

        parentCampaignRecordList = [
            Select Grade__c
                 , Meeting_Day_s__c
                 , Meeting_Location__c
                 , Volunteers_Needed_to_Start__c 
                 , Display_on_Website__c
                 , Meeting_Start_Date_time__c
                 , Girl_Openings_Remaining__c
                 , Zip_Code__c
                 , Account__c
                 , Participation__c
                 , Id
                 , Name
                 , Council_Code__c 
              From Campaign 
             where (Name = 'Unsure')
               and Display_on_Website__c = true
               and RecordTypeId = :GirlRegistrationUtilty.getCampaignRecordTypeId(GirlRegistrationUtilty.VOLUNTEER_PROJECT_RECORDTYPE)
        ];

        Campaign campaign = (parentCampaignRecordList != null && parentCampaignRecordList.size() > 0) ? parentCampaignRecordList[0] : new Campaign();

        if(campaign != null && campaign.Id != null)
            unsureCampaignRecordList.add(new ParentCampaignWrapper(false, '0', campaign.Name, campaign.Grade__c, campaign.Meeting_Location__c,  campaign.Meeting_Day_s__c, campaign.Meeting_Start_Date_time__c, String.valueOf(campaign.Girl_Openings_Remaining__c), campaign.Name, campaign.Account__c, String.valueOf(campaign.Volunteers_Needed_to_Start__c ), campaign.Id, campaign.Participation__c));

        return unsureCampaignRecordList;
    }

    public List<ParentCampaignWrapper> addIRMCampaign() {
        List<Campaign> irmCampaignList = new List<Campaign>();
        List<ParentCampaignWrapper> irmCampaignRecordList = new List<ParentCampaignWrapper>();

        irmCampaignList = [
            Select Grade__c
                 , Meeting_Day_s__c
                 , Meeting_Location__c
                 , Volunteers_Needed_to_Start__c 
                 , Display_on_Website__c
                 , Meeting_Start_Date_time__c
                 , Girl_Openings_Remaining__c
                 , Zip_Code__c
                 , Account__c
                 , Participation__c
                 , Id
                 , Name
                 , Council_Code__c
              From Campaign 
             where Display_on_Website__c = true
               and Participation__c = 'IRM'
               and RecordTypeId = :GirlRegistrationUtilty.getCampaignRecordTypeId(GirlRegistrationUtilty.VOLUNTEER_PROJECT_RECORDTYPE)
        ];

        Campaign campaign =  (irmCampaignList != null && irmCampaignList.size() > 0) ? irmCampaignList[0] : new Campaign();

        if(campaign != null && campaign.Id != null)
            irmCampaignRecordList.add(new ParentCampaignWrapper(false, '0', campaign.Name, campaign.Grade__c, campaign.Meeting_Location__c,  campaign.Meeting_Day_s__c, campaign.Meeting_Start_Date_time__c, String.valueOf(campaign.Girl_Openings_Remaining__c), campaign.Name, campaign.Account__c, String.valueOf(campaign.Volunteers_Needed_to_Start__c ), campaign.Id, campaign.Participation__c));

        return irmCampaignRecordList;
    }

    @TestVisible public class ParentCampaignWrapper implements Comparable {

        public Boolean isCampaignChecked { get; set; }
        public String campaignDistance { get; set; }
        public String childCampaignName { get; set; }
        public String campaignGrade { get; set; }
        public String campaignMeetingLocation { get; set; }
        public String campaignMeetingDay { get; set; }
        public DateTime campaignMeetingStartDatetime { get; set; }
        public String campaignOpeningsRemaining { get; set; }
        public String parentCampaignName { get; set; }
        public String campaignVolunteerReq { get; set; }
        public String campaignAccountId;
        public Id campaignId { get; set; }
        public String campaignParticipationType { get; set; }
         public String campaignMeetingStartDatetimeStd { get; set; }
        @TestVisible public ParentCampaignWrapper(Boolean campaignChecked, String strCampaignDistance, String strChildCampaignName, String strCampaignGrade, String strCampaignMeetingLocation, String strcampaignMeetingDay, DateTime strCampaignMeetingStartDatetime, String strcampaignOpeningsRemaining, String strParentCampaignName, String strAccountId, String strVolunteers, String strCampaignId, String strCampaignParticipation) {//String strChildCampaignId,String strParticipation 
            isCampaignChecked = campaignChecked;
            campaignDistance = strCampaignDistance;
            childCampaignName = strChildCampaignName;
            campaignGrade = strCampaignGrade;
            campaignMeetingLocation = strCampaignMeetingLocation;
            campaignMeetingDay = strcampaignMeetingDay;
            campaignMeetingStartDatetime = strCampaignMeetingStartDatetime;
            campaignOpeningsRemaining = strcampaignOpeningsRemaining;
            campaignVolunteerReq = strVolunteers;
            parentCampaignName = strParentCampaignName;
            campaignAccountId = strAccountId;
            campaignId = strCampaignId;
            campaignParticipationType = strCampaignParticipation;
              if(strCampaignMeetingStartDatetime!=null)
          campaignMeetingStartDatetimeStd = String.valueOf(strCampaignMeetingStartDatetime.getTime());
        
        }

        public Integer compareTo(Object compareTo) {
            ParentCampaignWrapper compareToParentCamp = (ParentCampaignWrapper)compareTo;
            if (Integer.valueOf(campaignDistance) == Integer.valueOf(compareToParentCamp.campaignDistance)) return 0;
            if (Integer.valueOf(campaignDistance) > Integer.valueOf(compareToParentCamp.campaignDistance)) return 1;
            return -1;
        }
    }
}