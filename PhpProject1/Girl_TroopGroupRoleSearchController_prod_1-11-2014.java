public with sharing class Girl_TroopGroupRoleSearchController extends SobjectExtension {

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
     public List<ParentCampaignWrapper> parentCampaignWrapperList2 { get; set; }
     public Boolean showselectedcampaign { get; set; }
     public Boolean isunsure { get; set; }
     public ID deleteselectedrecordid { get; set; }
     public ID selectedcampaignidd {get; set; }
    public static final Map<String, Schema.FieldSet> FIELDSETS_CAMPAIGN = SObjectType.Campaign.FieldSets.getMap();
    
    private String contactId;
    private String girlContactId;
    private String parentContactId;
    private String councilId { get; set; }
    private static Integer counterUnableToLockRow = 0;

    public Girl_TroopGroupRoleSearchController() {
        counterUnableToLockRow = 0;
        selectedPageSize = '10';
        selectedRadius = string.valueOf(5);
        showSearchResultTable = false;
        whyAreYouUnsure = '';
        pagerFlag = false;
        
        showselectedcampaign =false;
        pageNumberSet = new List<Integer>();
        parentCampaignWrapperList = new List<ParentCampaignWrapper>();
         parentCampaignWrapperList2 = new List<ParentCampaignWrapper>();

        if(Apexpages.currentPage().getParameters().containsKey('GirlContactId'))
            girlContactId = Apexpages.currentPage().getParameters().get('GirlContactId');

        if(Apexpages.currentPage().getParameters().containsKey('ParentContactId'))
            parentContactId = ApexPages.CurrentPage().getParameters().get('ParentContactId');

        if(Apexpages.currentPage().getParameters().containsKey('CouncilId'))
            councilId = Apexpages.currentPage().getParameters().get('CouncilId');

        if(councilId != null && councilId != '')
            Girl_RegistrationHeaderController.councilAccount = GirlRegistrationUtilty.getCouncilAccount(councilId);

        Contact girlContact = getContactRecord();

        if(girlContact != null && girlContact.Id != null) {
            zipCode = (girlContact.MailingPostalCode != null && girlContact.MailingPostalCode != '') ? girlContact.MailingPostalCode.substring(0, 5) : girlContact.MailingPostalCode;
            Grade = girlContact.Grade__c;
        }
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
                 return addErrorMessageAndRollback(savepoint,'No Troop/Group with this name exists. Try utilizing the typeahead in the Troop/Group field or search by Zip Code and Radius.');
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
                     return addErrorMessageAndRollback(savepoint,'No Troop/Group with this name exists. Try utilizing the typeahead in the Troop/Group field or search by Zip Code and Radius.');
                }
            }
        } catch(System.exception pException) {
            return addErrorMessageAndRollback(savepoint, pException);
        }
        return null;
    }

    public PageReference showDetails() {
        Savepoint savepoint = Database.setSavepoint();
        Set<String> selectedFields = addSelectedFields(new Set<String>(), FIELDSETS_CAMPAIGN.get('displayGirlTroopDetails'));
        try{
            campaignPopup = new Campaign();
            Campaign[] campaignList = (Campaign[]) Database.query(''
            + 'SELECT ' + generateFieldSelect(selectedFields)
            + '  FROM Campaign'
            + ' WHERE Id = '+'\''+campaignDetailsId+'\''
            + '   AND Display_on_Website__c = true'
        );
            campaignPopup = campaignList.size() > 0 ? campaignList[0] : null;
        } catch(System.exception pException) {
            return addErrorMessageAndRollback(savepoint, pException);
        }

        return null;
    }

    public PageReference clearSelections() {
        pagerFlag = false;
        parentCampaignWrapperList.clear();
         parentCampaignWrapperList2.clear();
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
        //radius.add(new Selectoption('--None--', '--None--'));
        radius.add(new SelectOption('5', '5'));
        radius.add(new SelectOption('10', '10'));
        radius.add(new SelectOption('15', '15'));
        radius.add(new SelectOption('20', '20'));
        return radius;
    }

    public double calcDistance(double latA, double longA, double latB, double longB) { 
        double radian = 57.295; 
        double theDistance = (Math.sin(latA/radian) * Math.sin(latB/radian) + Math.cos(latA/radian) * Math.cos(latB/radian) * Math.cos((longA - longB)/radian));
          if(theDistance >1.0){
             theDistance=1.0;
          }  
          if(theDistance <-1.0){
             theDistance=-1.0;
          } 
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

     public PageReference deleteselectedrecord(){
            Integer ii=0;
            if(parentCampaignWrapperList2 != null && parentCampaignWrapperList2.size() > 0 ) {
                    for(Integer i=0;i< parentCampaignWrapperList2.size();i++) {
                            if(parentCampaignWrapperList2[i].campaignId==deleteselectedrecordid)
                            {
                            
                            parentCampaignWrapperList2.remove(i);
                            
                            }
                            
                            
                       }
                 }
                 
             return null;
            }
            
 public PageReference displayselectedcampaign(){
    showselectedcampaign =true;
    system.debug('displayselectedcampaign==>run');
    if(parentCampaignWrapperList != null && parentCampaignWrapperList.size() > 0 ) {
            for(ParentCampaignWrapper wrapper : parentCampaignWrapperList) {
                if(wrapper.campaignId==selectedcampaignidd ){
                       
                              if(parentCampaignWrapperList2 != null && parentCampaignWrapperList2.size() > 0 ) {
                                        for(ParentCampaignWrapper wrapper2 : parentCampaignWrapperList2) {
                                            if(wrapper2.campaignId==selectedcampaignidd ){
                                               showselectedcampaign =false;
                                            }
                                        }
                                        if(showselectedcampaign ==true){
                                        
                                                if(wrapper.childCampaignName == 'Unsure')
                                                {
                                                         parentCampaignWrapperList2.clear();
                                                        isunsure =true;
                                                }
                                                 if(wrapper.campaignParticipationType == 'IRM')
                                                {       // to remove other , unsure
                                                    isunsure =false;
                                                   if(parentCampaignWrapperList2 != null && parentCampaignWrapperList2.size() > 0 )                                                       //remove unsure , irm
                                                     { Integer k=0;
                                                    Integer ssize=parentCampaignWrapperList2.size();
                                                    //system.debug('outsideirm list size'+parentCampaignWrapperList2.size());
                                                     for(Integer l=0;l< ssize ;l++) {
                                                      if(parentCampaignWrapperList2 != null && parentCampaignWrapperList2.size() > 0 )                                                       //remove unsure , irm
                                                     {
                                                        // system.debug('outside irm i:'+l);
                                                           if(parentCampaignWrapperList2[l].campaignParticipationType == 'Troop' || parentCampaignWrapperList2[l].childCampaignName == 'Unsure')
                                                               {
                                                          parentCampaignWrapperList2.remove(l);
                                                             l--;
                                                                 }
                                                                
                                                          }
                                                    
                                                         }
                                                     } 
                                                }
                                               if((wrapper.campaignParticipationType == 'Troop') && (wrapper.childCampaignName != 'Unsure') )
                                                {    isunsure =false;
                                                   if(parentCampaignWrapperList2 != null && parentCampaignWrapperList2.size() > 0 )                                                       //remove unsure , irm
                                                    {
                                                          for(Integer j=0;j< parentCampaignWrapperList2.size();j++) {
                                                          if(parentCampaignWrapperList2[j].campaignParticipationType == 'IRM' || parentCampaignWrapperList2[j].childCampaignName == 'Unsure'){
                                                            
                                                             parentCampaignWrapperList2.remove(j);
                                                            }
                                                        
                                                           }
                                                    }
                                                }
                                        
                                        
                                             parentCampaignWrapperList2.add(wrapper);
                                        }
                            
                                 }else{     if(wrapper.childCampaignName == 'Unsure')
                                                {
                                                        isunsure =true;
                                                }else{   isunsure =false;                    }
                                        parentCampaignWrapperList2.add(wrapper);
                                 }                           
                }  
            }
        }
        system.debug('unsure===>'+isunsure);
        system.debug('parentCampaignWrapperList2 ==>run'+parentCampaignWrapperList2);
    return null;
    }

    public Pagereference addCampaignMember() {

        counterUnableToLockRow++;
        Savepoint savepoint = Database.setSavepoint();

      
        if(parentCampaignWrapperList2 != null && parentCampaignWrapperList2.size() > 0 ) { }
       else {
             return addErrorMessageAndRollback(savepoint,'Please select Troop');
            }

        try {
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

                for(ParentCampaignWrapper wrapper : parentCampaignWrapperList2) {
                    if(wrapper.campaignParticipationType != null && wrapper.campaignParticipationType != '')
                        if(wrapper.campaignParticipationType.equalsIgnoreCase('IRM'))
                            isTroopContainsIRM = true;
                }

                for(ParentCampaignWrapper wrapper : parentCampaignWrapperList2) {

                   

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
                campaignMemberSaveresultList = Database.insert(newCampaignMemberList);

                if(campaignMemberIdSet.size() > 0) {
                    for(Id campaignMemberId : campaignMemberIdSet)
                        campaignMemberIds = campaignMemberIds == '' ?  string.valueOf(campaignMemberId) : campaignMemberIds + ',' + string.valueOf(campaignMemberId);
                }

                if(newCampaignMemberList != null && newCampaignMemberList.size() > 0) {
                    for(CampaignMember campaignMember : newCampaignMemberList) 
                        campaignMemberIds = campaignMemberIds == '' ?  string.valueOf(campaignMember.Id) : campaignMemberIds + ',' + string.valueOf(campaignMember.Id);
                }
                
                if(parentContactId != null) {
                    List<Contact> parentconactList = [Select Name, Id ,Get_Involved_Complete__c from Contact where Id = :parentContactId]; 
                    Contact parentContact = (parentconactList != null && parentconactList.size() > 0) ? parentconactList[0] : new Contact();
                    parentContact.Get_Involved_Complete__c = true;
                    update parentContact;
                    if(parentContact != null && parentContact.Id != null)
                        GirlRegistrationUtilty.updateSiteURLAndContactForGirl('/Girl_JoinMembershipInformation' + '?GirlContactId='+girlContactId + '&ParentContactId='+parentContactId+'&CampaignMemberIds='+CampaignMemberIds+'&CouncilId='+CouncilId, parentContact);
                }
                if(campaignMemberSaveresultList != null) {
                    for(Database.Saveresult sr : campaignMemberSaveresultList) {
                        if (sr.isSuccess()) {
                            
                            Pagereference JoinMembershipInformationPage = Page.Girl_JoinMembershipInformation;
                            //Pagereference JoinMembershipInformationPage = new Pagereference('/apex/Girl_JoinMembershipInformation');
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
                    Pagereference JoinMembershipInformationPage = Page.Girl_JoinMembershipInformation;
                    //Pagereference JoinMembershipInformationPage = new Pagereference('/apex/Girl_JoinMembershipInformation');
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
            if(pException.getMessage().contains('UNABLE_TO_LOCK_ROW')){
                if(counterUnableToLockRow < 4) {
                    Database.rollback(savepoint);
                    return addCampaignMember();
                }
                else {
                    Database.rollback(savepoint);
                    return addErrorMessage('Record is locked by another user. Please re-submit the page once more.');
                }
            }
            else
                return addErrorMessageAndRollback(savepoint, pException);
        }
        counterUnableToLockRow = 0;
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

                for(ParentCampaignWrapper wrapper : parentCampaignWrapperList2) {

                    
                        campaignIdSet.add(wrapper.campaignId);
                        CampaignMember campaignMember = new CampaignMember(ContactId = girlContactId, CampaignId= wrapper.campaignId); //RecordTypeId = RT_SHEDULED_VOLUNTEER_ID

                        if(whyAreYouUnsure != null && whyAreYouUnsure != '')
                            campaignMember.Why_are_you_unsure__c = whyAreYouUnsure;

                        campaignMemberList.add(campaignMember);

                        Task task = new Task(Subject = 'Unsure-'+ contact.Name , WhoId = girlContactId,  OwnerId = contact.OwnerId, WhatId = wrapper.campaignId);
                        taskList.add(task);
                   
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
                }
                catch(DmlException dmlException) {
                    system.debug('dmlException===>'+dmlException);
                    return addErrorMessageAndRollback(savepoint,dmlException.getMessage());
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

                if(parentContactId != null) {
                    List<Contact> parentconactList = [Select Name, Id from Contact where Id = :parentContactId]; 
                    Contact parentContact = (parentconactList != null && parentconactList.size() > 0) ? parentconactList[0] : new Contact();
                    try{
                        parentContact.Get_Involved_Complete__c = true;
                        update parentContact;
                    }catch(DmlException dmlException) {
                        return addErrorMessageAndRollback(savepoint,dmlException.getMessage());
                    }
                    
                    
                    if(parentContact != null && parentContact.Id != null)
                        GirlRegistrationUtilty.updateSiteURLAndContactForGirl('/Girl_JoinMembershipInformation' + '?GirlContactId='+girlContactId + '&ParentContactId='+parentContactId+'&CampaignMemberIds='+CampaignMemberIds+'&CouncilId='+CouncilId, parentContact);
                }
                if(campaignMemberSaveresultList != null) {
                    for (Database.Saveresult sr : campaignMemberSaveresultList) {
                        if (sr.isSuccess()) {
                            Pagereference JoinMembershipInformationPage = Page.Girl_JoinMembershipInformation;
                            //Pagereference JoinMembershipInformationPage = new Pagereference('/apex/Girl_JoinMembershipInformation');
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
                    Pagereference JoinMembershipInformationPage = Page.Girl_JoinMembershipInformation;//new Pagereference('/apex/Girl_JoinMembershipInformation');
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

            /*List<Campaign> allChildCampaignWithZipCodeList = GirlRegistrationUtilty.getListOfCampaign(troopOrGroupName, 'TroopNameAndZipCode');
            system.debug('allChildCampaignWithZipCodeList===>'+allChildCampaignWithZipCodeList);
            innerParentCampaignWrapperList.addAll(getAllCampaignWithMatchedCriteria(allChildCampaignWithZipCodeList));*/
            
            Zip_Code__c selectedZipCode = GirlRegistrationUtilty.getZipCode(zipCode);
            system.debug('selectedZipCode===>'+selectedZipCode);
            if(selectedZipCode != null && selectedZipCode.geo_location__Latitude__s != null && selectedZipCode.geo_location__Longitude__s != null) {
                system.debug('INside===>');
                List<Zip_Code__c> zipCodeList = GirlRegistrationUtilty.getAllZipCodeWithingSelectedRadius(String.valueOf(selectedZipCode.geo_location__Latitude__s), String.valueOf(selectedZipCode.geo_location__Longitude__s), selectedRadius);
                system.debug('zipCodeList======>'+zipCodeList);
                
                if(!zipCodeList.isEmpty()) {
                    Set<String> zipCodeSet = new Set<String>();
                
                    for(Zip_Code__c zipCode : zipCodeList) {
                        zipCodeSet.add(zipCode.Zip_Code_Unique__c);
                        system.debug('zipCode.Zip_Code_Unique__c=====>'+zipCode.Zip_Code_Unique__c);
                    }
                
                    if(!zipCodeSet.isEmpty()) {
                        List<Campaign> allChildCampaignWithZipCodeList = GirlRegistrationUtilty.getListOfCampaign(troopOrGroupName, 'TroopNameAndZipCode', zipCodeSet, Grade);
                        system.debug('allChildCampaignWithZipCodeList===>'+allChildCampaignWithZipCodeList);
                        innerParentCampaignWrapperList.addAll(getAllCampaignWithMatchedCriteria(allChildCampaignWithZipCodeList));
                    }
                }
            }
        }
        else if(troopOrGroupName != null && troopOrGroupName != '') {
					      List<Campaign> allCampaignList;
                if(councilId !=null && councilId !='')
                {
                 allCampaignList = [
                                Select Grade__c
                                     , Meeting_Day_s__c
                                     , Meeting_Frequency__c
                                     , Meeting_Location__c
                                     , Volunteers_Needed_to_Start__c 
                                     , Display_on_Website__c
                                     , Meeting_Start_Date_time__c
                                    ,Troop_Start_Date__c
                                    ,Meeting_Start_Time__c
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
                                   and Account__c = :councilId
                            ];
                 }else{
                  allCampaignList = GirlRegistrationUtilty.getListOfCampaign(troopOrGroupName, 'TroopName', new Set<String>(), Grade);
                 }
       //     List<Campaign> allCampaignList = GirlRegistrationUtilty.getListOfCampaign(troopOrGroupName, 'TroopName', new Set<String>(), Grade);
            system.debug('allCampaignList===>'+allCampaignList);
            if(allCampaignList != null && allCampaignList.size() > 0) {
                for(Campaign campaign : allCampaignList)                     
                    innerParentCampaignWrapperList.add(new ParentCampaignWrapper(false, '0', campaign.Name, campaign.Grade__c, campaign.Meeting_Location__c,  campaign.Meeting_Day_s__c, campaign.Meeting_Start_Date_time__c, String.valueOf(campaign.Girl_Openings_Remaining__c), campaign.Name, campaign.Account__c, String.valueOf(campaign.Volunteers_Needed_to_Start__c ), campaign.Id, campaign.Participation__c));
                  
                  innerParentCampaignWrapperList.sort();
            }

            innerParentCampaignWrapperList.addAll(addUnsureCampaign());
            innerParentCampaignWrapperList.addAll(addIRMCampaign());
        }
        else if(zipCode != null && zipCode != '') {
            
            Zip_Code__c selectedZipCode = GirlRegistrationUtilty.getZipCode(zipCode);
            system.debug('selectedZipCode===>'+selectedZipCode);
            if(selectedZipCode != null && selectedZipCode.geo_location__Latitude__s != null && selectedZipCode.geo_location__Longitude__s != null) {
                system.debug('INside===>');
                List<Zip_Code__c> zipCodeList = GirlRegistrationUtilty.getAllZipCodeWithingSelectedRadius(String.valueOf(selectedZipCode.geo_location__Latitude__s), String.valueOf(selectedZipCode.geo_location__Longitude__s), selectedRadius);
                system.debug('zipCodeList======>'+zipCodeList);
                
                Set<String> zipCodeSet = new Set<String>();
                
                for(Zip_Code__c zipCode : zipCodeList) {
                    zipCodeSet.add(zipCode.Zip_Code_Unique__c);
                    system.debug('zipCode.Zip_Code_Unique__c=====>'+zipCode.Zip_Code_Unique__c);
                }
                system.debug('zipCodeSet======>'+zipCodeSet);
                
                if(!zipCodeSet.isEmpty()) {
                    List<Campaign> allCampaignWithZipCodeList = GirlRegistrationUtilty.getListOfCampaign('', 'ZipCode', zipCodeSet, Grade);
                    system.debug('allCampaignWithZipCodeList======>'+allCampaignWithZipCodeList);
                    
                    innerParentCampaignWrapperList.addAll(getAllCampaignWithMatchedCriteria(allCampaignWithZipCodeList));
                }
            }
            
            /*List<Campaign> allCampaignWithZipCodeList = GirlRegistrationUtilty.getListOfCampaign('', 'ZipCode');
            innerParentCampaignWrapperList.addAll(getAllCampaignWithMatchedCriteria(allCampaignWithZipCodeList));*/
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

                        if(!gradeList.isEmpty() && gradeList.size() > 0){
                            for(String objGrade : gradeList) {
                                if(objGrade.contains(Grade)) {
                                    tempParentCampaignWrapperList.add(new ParentCampaignWrapper(false, campaignIdVSDistanceMap.get(campaign.Id), campaign.Name, campaign.Grade__c, campaign.Meeting_Location__c,  campaign.Meeting_Day_s__c, campaign.Meeting_Start_Date_time__c, String.valueOf(campaign.Girl_Openings_Remaining__c), campaign.Name, campaign.Account__c, String.valueOf(campaign.Volunteers_Needed_to_Start__c ), campaign.Id, campaign.Participation__c));
                                    break;
                                }
                            }
                        }
                    }
                    else {
                        tempParentCampaignWrapperList.add(new ParentCampaignWrapper(false, campaignIdVSDistanceMap.get(campaign.Id), campaign.Name, campaign.Grade__c, campaign.Meeting_Location__c,  campaign.Meeting_Day_s__c, campaign.Meeting_Start_Date_time__c, String.valueOf(campaign.Girl_Openings_Remaining__c), campaign.Name, campaign.Account__c, String.valueOf(campaign.Volunteers_Needed_to_Start__c ), campaign.Id, campaign.Participation__c));
                    }
                }
            }
            
            tempParentCampaignWrapperList.sort();
        }
        tempParentCampaignWrapperList.addAll(addUnsureCampaign());
        tempParentCampaignWrapperList.addAll(addIRMCampaign());
        return tempParentCampaignWrapperList;
    }

    public map<Id, String> calculateDistanceForEachCampaign(List<Campaign> allCampaignWithZipCodeList) {
        set<String> campaignZipCodeSet = new set<String>();
        //map<String, Id> zipCodeVSCampaignIdMap = new map<String, Id>();
        
        map<String, Set<Id>> zipCodeVSCampaignIdMap = new map<String, Set<Id>>();
        
        map<Id, String> campaignIdVSDistanceMap = new map<Id, String>();
        Zip_Code__c objZipCode = new Zip_Code__c();

        if(zipCode != null)
            objZipCode = GirlRegistrationUtilty.getZipCode(zipCode);

        if(allCampaignWithZipCodeList != null && allCampaignWithZipCodeList.size() > 0) {

            for(Campaign campaign : allCampaignWithZipCodeList) {
                
                if(zipCodeVSCampaignIdMap.containsKey(campaign.Zip_Code__c)) {
                    Set<Id> campaignIdSet = zipCodeVSCampaignIdMap.get(campaign.Zip_Code__c);
                    campaignIdSet.add(campaign.Id);
                    zipCodeVSCampaignIdMap.put(campaign.Zip_Code__c, campaignIdSet);
                }
                else {
                    Set<Id> campaignIdSet = new Set<Id>();
                    campaignIdSet.add(campaign.Id);
                    zipCodeVSCampaignIdMap.put(campaign.Zip_Code__c, campaignIdSet);
                }
                //zipCodeVSCampaignIdMap.put(campaign.Zip_Code__c, campaign.Id);
                
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
                                if(selectedRadius.toUpperCase().contains('NONE')){
                                    Set<Id> campaignIdSet = zipCodeVSCampaignIdMap.get(zipCodeNew.Zip_Code_Unique__c);
                                    for(Id campaignId : campaignIdSet)
                                        campaignIdVSDistanceMap.put(campaignId, String.valueOf(Integer.valueOf(distance)));
                                }
                                else if(Integer.valueOf(distance) <= Integer.valueOf(selectedRadius)) {
                                    Set<Id> campaignIdSet = zipCodeVSCampaignIdMap.get(zipCodeNew.Zip_Code_Unique__c);
                                    for(Id campaignId : campaignIdSet)
                                        campaignIdVSDistanceMap.put(campaignId, String.valueOf(Integer.valueOf(distance)));
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
    public static List<String> searchCampaingNames(String searchtext1,String councilId2) {
        String JSONString;
        List<Campaign> campaignList = new List<Campaign>();
        List<String> nameList = new List<String>();
        Id recTypeId = GirlRegistrationUtilty.getCampaignRecordTypeId(GirlRegistrationUtilty.VOLUNTEER_PROJECT_RECORDTYPE);

        Savepoint savepoint = Database.setSavepoint();

        try{
            String searchQueri = 'Select ParentId,Account__c, Name From Campaign Where Name Like \'%'+searchText1+'%\'  and Display_on_Website__c = true and RecordTypeId = \'' + recTypeId + '\' order by Name limit 100' ;
            campaignList = database.query(searchQueri);

            if(!campaignList.isEmpty())
                for(Campaign campaign : campaignList)
                { 
                    if(campaign.Account__c==councilId2)
                    nameList.add(campaign.Name);
                }
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
             limit 1
        ];
        system.debug('parentCampaignRecordList--->'+parentCampaignRecordList);
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

    public class ParentCampaignWrapper implements Comparable {

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

        public ParentCampaignWrapper(Boolean campaignChecked, String strCampaignDistance, String strChildCampaignName, String strCampaignGrade, String strCampaignMeetingLocation, String strcampaignMeetingDay, DateTime strCampaignMeetingStartDatetime, String strcampaignOpeningsRemaining, String strParentCampaignName, String strAccountId, String strVolunteers, String strCampaignId, String strCampaignParticipation) {//String strChildCampaignId,String strParticipation 
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