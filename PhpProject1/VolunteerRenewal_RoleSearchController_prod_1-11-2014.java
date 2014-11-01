public class VolunteerRenewal_RoleSearchController extends SobjectExtension{
    public String serachHeading { get; set;}
    public String troopOrGroupName { get; set;}
    public String zipCode { get; set;}
    public String selectedRadius { get; set;}
    public String whyAreYouUnsure { get; set; }
    public String campaignDetailsId { get; set; }
    public Campaign campaignPopup { get; set; }
    public Boolean pagerFlag {get; set;}
    public List<Campaign> campaignList { get; set;} 
    public List<Campaign> parentCampaignList { get; set;}
    public List<ParentCampaignWrapper> parentCampaignWrapperList2{ get; set;}
    public List<ParentCampaignWrapper> parentCampaignWrapperList { get; set;}
    public Boolean showSearchResultTable {get; set;}
    public List<Integer> pageNumberSet {get; set;}
    public String selectedPageSize {get; set;}
    public String selectedPageNumber {get; set;}
    public Contact loggedInContact;
    public Zip_Code__c zipCodeCouncilAccount;
    public boolean searchByCampaignName ;
     public ID deleteselectedrecordid { get; set; }
     public ID selectedcampaignidd {get; set; }
     public Boolean showselectedcampaign { get; set; }
     public Boolean isunsure { get; set; }
    private String contactId;
    private String councilId;
	public String councilId2 { get; set; }
    private List<ParentCampaignWrapper> unsureCampaignRecordList;
    private List<Contact> contactList;
    private String campaignMemberIds = '';
    private static Integer counterUnableToLockRow = 0;
    
    public static final Map<String, Schema.FieldSet> FIELDSETS_CAMPAIGN = SObjectType.Campaign.FieldSets.getMap();
    private static final map<String, Schema.RecordTypeInfo> CAMPAIGN_RECORDTYPE_INFO_MAP =  Campaign.SObjectType.getDescribe().getRecordTypeInfosByName();
    private static final String RT_VOLUNTEER_JOBS = 'Volunteer Jobs';
    private static final String RT_VOLUNTEER_PROJECT = 'Volunteer Project';
    public static final String RT_VOLUNTEER_JOBS_ID = getCampaignRecordTypeId(RT_VOLUNTEER_JOBS);
    public static final String RT_VOLUNTEER_PROJECT_ID = getCampaignRecordTypeId(RT_VOLUNTEER_PROJECT);

    private static final map<String, Schema.RecordTypeInfo> CONTACT_RECORDTYPE_INFO_MAP =  Contact.SObjectType.getDescribe().getRecordTypeInfosByName();
    private static final String RT_HOUSEHOLD = 'Household';
    public static final String RT_HOUSEHOLD_ID = getContactRecordTypeId(RT_HOUSEHOLD);
    
    private static final map<String, Schema.RecordTypeInfo> ACCOUNT_RECORDTYPE_INFO_MAP =  Account.SObjectType.getDescribe().getRecordTypeInfosByName();
    private static final String RT_COUNCIL = 'Council';
    public static final String RT_COUNCIL_ID = getAccountRecordTypeId(RT_COUNCIL);

    private static final map<String, Schema.RecordTypeInfo> CAMPAIGNMEMBER_RECORDTYPE_INFO_MAP =  CampaignMember.SObjectType.getDescribe().getRecordTypeInfosByName();
    private static final String RT_SHEDULED_VOLUNTEER = 'Scheduled Volunteer';
    public static final String RT_SHEDULED_VOLUNTEER_ID = getCampaignMemberRecordTypeId(RT_SHEDULED_VOLUNTEER);

    private static String getCampaignRecordTypeId(String name) {
       return (CAMPAIGN_RECORDTYPE_INFO_MAP.get(name) != null) ? CAMPAIGN_RECORDTYPE_INFO_MAP.get(name).getRecordTypeId() : null;
    }

    private static String getContactRecordTypeId(String name) {
       return (CONTACT_RECORDTYPE_INFO_MAP.get(name) != null) ? CONTACT_RECORDTYPE_INFO_MAP.get(name).getRecordTypeId() : null;
    }
    
    private static String getAccountRecordTypeId(String name) {
       return (ACCOUNT_RECORDTYPE_INFO_MAP.get(name) != null) ? ACCOUNT_RECORDTYPE_INFO_MAP.get(name).getRecordTypeId() : null;
    }

    private static String getCampaignMemberRecordTypeId(String name) {
       return (CAMPAIGNMEMBER_RECORDTYPE_INFO_MAP.get(name) != null) ? CAMPAIGNMEMBER_RECORDTYPE_INFO_MAP.get(name).getRecordTypeId() : null;
    }

    public VolunteerRenewal_RoleSearchController() {
        system.debug('User ######'+Userinfo.getUserName());
        system.debug('Userinfo.getUserId()###'+Userinfo.getUserId());
        searchByCampaignName = false;
        counterUnableToLockRow = 0;
        selectedPageSize = '10';
        showSearchResultTable = false;
        showselectedcampaign =false;
        whyAreYouUnsure = '';
        pagerFlag = false;
        loggedInContact = new Contact();
        campaignMemberIds = '';
        parentCampaignWrapperList2= new List<ParentCampaignWrapper>();
        pageNumberSet = new List<Integer>();
        parentCampaignWrapperList = new List<ParentCampaignWrapper>();
        unsureCampaignRecordList = new List<ParentCampaignWrapper>();
        zipCodeCouncilAccount = new Zip_Code__c();
        
        User user = getCurrentUser();
        system.debug('user==>'+user);
        if(user.Id != NULL && user.contactId != NULL)
            contactList = VolunteerRenewalUtility.getContactList(user.contactId);
        if(contactList != null && contactList.size() > 0)
            loggedInContact = contactList[0];
        system.debug('&&&&&&&&&&&&&&& contactList'+contactList);   
        if(contactList != null && contactList.size() > 0) {
            if(contactList[0].MailingPostalCode <> NULL)
                zipCodeCouncilAccount = VolunteerRenewalUtility.getZipCode(contactList[0].MailingPostalCode);
        }
            
        if(zipCodeCouncilAccount.Council__c <> NULL) 
            zipCode = zipCodeCouncilAccount.Council__c;
        
            
        if (Apexpages.currentPage().getParameters().containsKey('ContactId'))
            contactId = Apexpages.currentPage().getParameters().get('ContactId');

        //if (Apexpages.currentPage().getParameters().containsKey('CouncilId'))
            //councilId = Apexpages.currentPage().getParameters().get('CouncilId');

		 if (Apexpages.currentPage().getParameters().containsKey('CouncilId'))
            councilId2 = Apexpages.currentPage().getParameters().get('CouncilId');


        if (Apexpages.currentPage().getParameters().containsKey('CampaignMemberIds'))
            campaignMemberIds = Apexpages.currentPage().getParameters().get('CampaignMemberIds');
        system.debug('== campaignMemberIds ===:  ' + campaignMemberIds);
        
        if(councilId != null && councilId != '')
            VolunteerController.councilAccount = VolunteerRenewalUtility.getCouncilAccount(councilId);

        Contact objCcontact = getContactRecord();
        if(objCcontact != null && objCcontact.Id != null)
            zipCode = objCcontact.MailingPostalCode;

        //addUnsureCampaign();
    }
    
    public Pagereference skipNewRoles() {
        Pagereference JoinMembershipInformationPage = new Pagereference('/apex/VolunteerRenewal_MembershipInformation');
        if(loggedInContact != null)
            JoinMembershipInformationPage.getParameters().put('ContactId', loggedInContact.Id);
        if(campaignMemberIds != null && campaignMemberIds != '')
            JoinMembershipInformationPage.getParameters().put('CampaignMemberIds', campaignMemberIds);
        if(zipCodeCouncilAccount != null)
            JoinMembershipInformationPage.getParameters().put('CouncilId', zipCodeCouncilAccount.Council__c);
        JoinMembershipInformationPage.setRedirect(true);
                return JoinMembershipInformationPage;  
    }
    
    public PageReference searchTroopORGroupRoleByNameORZip() {
        Savepoint savepoint = Database.setSavepoint();
        try {
            pagerFlag = true; 
            if(councilId != null && councilId != '')
                VolunteerController.councilAccount = VolunteerRenewalUtility.getCouncilAccount(councilId);

            List<ParentCampaignWrapper> newTempWrapperList = new List<ParentCampaignWrapper>();
            newTempWrapperList = obtainParentCampaignWrapperList();
            
             if(searchByCampaignName == true) {
                if(newTempWrapperList.size() == 0)
                    parentCampaignWrapperList.clear();
                    List<Campaign> unsureCampaignList = [
                    Select Parent.Name
                 , ParentId
                 , Parent.Grade__c
                 , Parent.Meeting_Day_s__c
                 , Parent.Meeting_Location__c
                 , Parent.rC_Volunteers__Required_Volunteer_Count__c
                 , Parent.Display_on_Website__c
                 , Parent.Meeting_Start_Date_time__c
                 , Parent.Zip_Code__c
                 , Parent.Account__c
                 , Id
                 ,Parent.Participation__c
                 , Name
                 , Zip_Code__c
                 , Council_Code__c
                 , GS_Volunteers_Required__c 
                 , Volunteer_Openings_Remaining__c 
              From Campaign 
             where Parent.Name = 'Unsure'
               and ParentId != null
               and Display_on_Website__c = true
               and RecordTypeId = :GirlRegistrationUtilty.getCampaignRecordTypeId(GirlRegistrationUtilty.VOLUNTEER_JOBS_RECORDTYPE)
             limit 1
                ];
                for(Campaign campaign : unsureCampaignList){
                 parentCampaignWrapperList.add(new ParentCampaignWrapper(false, '0', campaign.Name, campaign.Parent.Grade__c, campaign.Parent.Meeting_Location__c,  campaign.Parent.Meeting_Day_s__c, campaign.Parent.Meeting_Start_Date_time__c, String.valueOf(campaign.Volunteer_Openings_Remaining__c), campaign.Parent.Name, campaign.Parent.Account__c, campaign.Id,campaign.Parent.Participation__c));
                }  
                                        
                 if(troopOrGroupName!='unsure' ){
                return addErrorMessageAndRollback(savepoint,'No Troop/Groups with this Name Exists.');
                 }else{
                 return null; }
                    
                    
               
             }
            if(newTempWrapperList.size() == 0) {
                parentCampaignWrapperList.clear();
                pagerFlag = false;
                return addErrorMessage('No Troop/Group found for this Zip code within the radius selected'); 
            }
            system.debug('***newTempWrapperList***'+newTempWrapperList);
            
            if(newTempWrapperList != null && newTempWrapperList.size() > 0) {

                fillRolesToDisplayPerPage(newTempWrapperList.size());

                parentCampaignWrapperList.clear();
                system.debug('***selectedPageSize***'+selectedPageSize);
                for(Integer recordSize = 0; recordSize < Integer.valueOf(selectedPageSize);  recordSize++) {
                    if(newTempWrapperList.size() > recordSize)
                        parentCampaignWrapperList.add(newTempWrapperList[recordSize]);
                }
                system.debug('***parentCampaignWrapperList***'+parentCampaignWrapperList);
                if(parentCampaignWrapperList == null || parentCampaignWrapperList.size() == 0) {
                    return addErrorMessageAndRollback(savepoint,'No Troop/Group found for this Zip code within the radius selected');
                }
            }
        } catch(System.exception pException) {
            return addErrorMessageAndRollback(savepoint, pException);
        }
        return null;
    }

    public PageReference showDetails() {
        Savepoint savepoint = Database.setSavepoint();
        Set<String> selectedFields = addSelectedFields(new Set<String>(), FIELDSETS_CAMPAIGN.get('displayTroopDetails'));
        try {
           campaignPopup = new Campaign();
            Campaign[] campaignList = (Campaign[]) Database.query(''
            + 'SELECT ' + generateFieldSelect(selectedFields)
            + '  FROM Campaign'
            + ' WHERE Id = '+'\''+campaignDetailsId+'\''
            + '   AND Display_on_Website__c = true'
            );

            campaignPopup = (campaignList.size() > 0 && campaignList != null) ? campaignList[0] : null;

           // if(campaignPopup != null && campaignPopup.Parent != null && campaignPopup.Grade__c != null)
              //  campaignPopup.Grade__c = campaignPopup.Grade__c;
        } catch(System.exception pException) {
            return addErrorMessageAndRollback(savepoint, pException);
        }        
        return null;
    }

    public PageReference clearSelections() {
        Savepoint savepoint = Database.setSavepoint();

        try {
            pagerFlag = false;
            parentCampaignWrapperList.clear();
             parentCampaignWrapperList2.clear();
            zipCode = '';
            troopOrGroupName = '';
        } catch(System.exception pException) {
            return addErrorMessageAndRollback(savepoint, pException);
        }  
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

        try {
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

                if(allParentCampaignWrapperList != null && allParentCampaignWrapperList.size() > 0) {
                    fillRolesToDisplayPerPage(allParentCampaignWrapperList.size());

                    for(Integer recordNumberTostart = 0; recordNumberTostart < pageSize ; recordNumberTostart++) {
                        if((recordAllreadyDisplayed + recordNumberTostart) < allParentCampaignWrapperList.size()) {
                            ParentCampaignWrapper wrapper = allParentCampaignWrapperList[recordAllreadyDisplayed + recordNumberTostart];
                            tempParentCampaignWrapperList.add(wrapper);
                        }
                    }

                    parentCampaignWrapperList.clear();
                    if(tempParentCampaignWrapperList != null && tempParentCampaignWrapperList.size() > 0)
                        parentCampaignWrapperList.addAll(tempParentCampaignWrapperList);
                }
                else {
                     return addErrorMessageAndRollback(savepoint,'No results to display');
                }
            }
            else if(selectedPageSize != null) {
                selectedPageNumber = '1';

                List<ParentCampaignWrapper> allParentCampaignWrapperList = new List<ParentCampaignWrapper>();

                Integer pageNumber = Integer.valueOf(selectedPageNumber);
                Integer pageSize = Integer.valueOf(selectedPageSize);
                Integer recordAllreadyDisplayed = (pageNumber - 1) * pageSize;

                allParentCampaignWrapperList = obtainParentCampaignWrapperList();
                if(allParentCampaignWrapperList != null && allParentCampaignWrapperList.size() > 0) {
                    fillRolesToDisplayPerPage(allParentCampaignWrapperList.size());

                    List<ParentCampaignWrapper> testParentCampaignWrapperList = new List<ParentCampaignWrapper>();

                    List<ParentCampaignWrapper> tempParentCampaignWrapperList = new List<ParentCampaignWrapper>();
                    for(Integer recordNumberTostart = 1; recordNumberTostart <= pageSize ; recordNumberTostart++) {
                        if((recordAllreadyDisplayed + recordNumberTostart) < allParentCampaignWrapperList.size()) {
                            ParentCampaignWrapper wrapper = allParentCampaignWrapperList[recordAllreadyDisplayed + recordNumberTostart];
                            tempParentCampaignWrapperList.add(wrapper);
                        }
                    }
                    system.debug('***tempParentCampaignWrapperList***'+tempParentCampaignWrapperList);
                    parentCampaignWrapperList.clear();
                    if(tempParentCampaignWrapperList != null && tempParentCampaignWrapperList.size() >0)
                        parentCampaignWrapperList.addAll(tempParentCampaignWrapperList);
                }
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
                                                        system.debug('enters inside1');
                                                   if(parentCampaignWrapperList2 != null && parentCampaignWrapperList2.size() > 0 )                                                       //remove unsure , irm
                                                    {       system.debug('enters inside2');
                                                          for(Integer j=0;j< parentCampaignWrapperList2.size();j++) {
                                                              system.debug('enters inside3');
                                                          if(parentCampaignWrapperList2[j].campaignParticipationType == 'IRM' || parentCampaignWrapperList2[j].childCampaignName == 'Unsure'){
                                                             system.debug('enters inside4');
                                                             parentCampaignWrapperList2.remove(j);
                                                            }
                                                        
                                                           }
                                                    }
                                                }
                                        
                                                 system.debug('enters inside5wrapper.campaignParticipationType'+wrapper.campaignParticipationType);
                                             parentCampaignWrapperList2.add(wrapper);
                                        }
                            
                                 }else{    
                                                if(wrapper.childCampaignName == 'Unsure')
                                                {
                                                        isunsure =true;
                                                }else{   isunsure =false;      
                                                }
                                                system.debug('enters inside6');
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
        system.debug('== 1. campaignMemberIds ===:  ' + campaignMemberIds);
         system.debug('==== parentCampaignWrapperList2.size() : ' + parentCampaignWrapperList2.size());
        try {
            
            if(contactId != null && contactId != '') {

                Contact contact = getContactRecord();
                system.debug('== contact ===:  ' + contact);

                List<Campaign> allChildCampaign = new List<Campaign>();
                List<Campaign> childCampaignsToUpdateList = new List<Campaign>();
                List<CampaignMember> campaignMemberList = new List<CampaignMember>();

                Set<Id> campaignIdSet = new Set<Id>();
                Set<Id> campaignMemberIdSet = new Set<Id>();
                map<String, CampaignMember> contactIdCampaignId_CampaignMemberMap = new map<String, CampaignMember>();

                system.debug('==parentCampaignWrapperList2===> ' + parentCampaignWrapperList2);
                
                if(parentCampaignWrapperList2 !=  null && parentCampaignWrapperList2.size() > 0) {
                    for(ParentCampaignWrapper wrapper : parentCampaignWrapperList2) {
                        
                            campaignIdSet.add(wrapper.childCampaignId);

                            system.debug('=== wrapper.parentCampaignName ===:  ' + wrapper.parentCampaignAccountId);
                            if(wrapper.parentCampaignAccountId != null) {
                                CampaignMember campaignMember = new CampaignMember(ContactId = contactId, CampaignId= wrapper.childCampaignId, Account__c = wrapper.parentCampaignAccountId); //RecordTypeId = RT_SHEDULED_VOLUNTEER_ID
                               // campaignMember.Continue_This_Position__c = 'Yes';
                                if(campaignMember != null)
                                    campaignMemberList.add(campaignMember);
                            }
                            else {
                                 return addErrorMessageAndRollback(savepoint,'Please contact the council for help with the \'' + wrapper.parentCampaignName + '\' role. Thank You.');
                            }
                       
                    }
                }
                system.debug('== campaignIdSet ===:  ' + campaignIdSet);

                List<CampaignMember> newCampaignMemberList = new List<CampaignMember>();

                List<CampaignMember> existingCampaignMemberList = GirlRegistrationUtilty.campaignMemberList(contactId, campaignIdSet);
                /* 
                [
                    Select Id
                         , contactId
                         , CampaignId
                      from CampaignMember 
                     where ContactId = :contactId 
                       and CampaignId IN :campaignIdSet
                ];
                */
                system.debug('==existingCampaignMemberList===> ' + existingCampaignMemberList);
                
                if(existingCampaignMemberList != null && existingCampaignMemberList.size() > 0) {
                    for(CampaignMember campaignMember : existingCampaignMemberList) {
                        if(campaignMember != null && campaignMember.contactId != null && campaignMember.CampaignId != null) {
                            String contactIdCampaignIdString = campaignMember.contactId + '' + campaignMember.CampaignId;
                            contactIdCampaignId_CampaignMemberMap.put(contactIdCampaignIdString, campaignMember);
                        }
                    }
                }

                system.debug('campaignMemberList   ===> ' + campaignMemberList);
                if(campaignMemberList != null && campaignMemberList.size() > 0) {
                    for(CampaignMember campaignMember : campaignMemberList) {
                        if(campaignMember != null && campaignMember.contactId != null && campaignMember.CampaignId != null) {
                            String contactIdCampaignIdString = campaignMember.contactId + '' + campaignMember.CampaignId;
                            if(contactIdCampaignId_CampaignMemberMap.containsKey(contactIdCampaignIdString)) {
                                campaignMemberIdSet.add(contactIdCampaignId_CampaignMemberMap.get(contactIdCampaignIdString).Id);
                            } else {
                                campaignMember.Welcome__c = true;
                                newCampaignMemberList.add(campaignMember);
                            }
                        }
                    }
                }else{   
                    return addErrorMessage('Please select troops');
                }
                system.debug('campaignMemberIdSet   ===> ' + campaignMemberIdSet);
                system.debug('newCampaignMemberList ===> ' + newCampaignMemberList);
                if(newCampaignMemberList != null && newCampaignMemberList.size() > 0)
                    VolunteerRenewalUtility.insertCampaignMemberList(newCampaignMemberList); 
                    //insert newCampaignMemberList;
                
                system.debug('newCampaignMemberList 1 ===> ' + newCampaignMemberList);
                
                if(contact != null) {
                    contact.Get_Involved_Complete__c = true;
                    update contact;                     
                }                
                system.debug('***campaignMemberIdsInSet***'+campaignMemberIdSet);
                if(campaignMemberIdSet != null && campaignMemberIdSet.size() > 0) {
                    for(Id campaignMemberId : campaignMemberIdSet) {
                        campaignMemberIds = (campaignMemberIds == '' || campaignMemberIds == null) ?  string.valueOf(campaignMemberId) : campaignMemberIds + ',' + string.valueOf(campaignMemberId);
                        system.debug('***campaignMemberIdsInSet***'+campaignMemberIds);
                    }
                }

                if(newCampaignMemberList != null && newCampaignMemberList.size() > 0) {
                    for(CampaignMember campaignMember : newCampaignMemberList) {
                        if(campaignMember != null && campaignMember.Id != null)
                            campaignMemberIds = (campaignMemberIds == '' || campaignMemberIds == null) ?  string.valueOf(campaignMember.Id) : campaignMemberIds + ',' + string.valueOf(campaignMember.Id);
                    }
                }
                system.debug('===campaignMemberIds ===> ' + campaignMemberIds);

                VolunteerRenewalUtility.updateSiteURLAndContact('VolunteerRenewal_MembershipInformation' + '?ContactId='+contactId + '&CouncilId='+councilId+'&CampaignMemberIds'+campaignMemberIds, contact);
                Pagereference JoinMembershipInformationPage = new Pagereference('/apex/VolunteerRenewal_MembershipInformation');
                if(loggedInContact != null)
                    JoinMembershipInformationPage.getParameters().put('ContactId', loggedInContact.Id);
                if(campaignMemberIds != null && campaignMemberIds != '')
                    JoinMembershipInformationPage.getParameters().put('CampaignMemberIds', campaignMemberIds);
                if(zipCodeCouncilAccount != null)
                    JoinMembershipInformationPage.getParameters().put('CouncilId', zipCodeCouncilAccount.Council__c);
                JoinMembershipInformationPage.setRedirect(true);
                return JoinMembershipInformationPage;
            }
            else{
                return addErrorMessageAndRollback(savepoint,'Please specify Contact');
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
            if(contactId != null && contactId != '') {
                List<Contact> conactList = VolunteerRenewalUtility.contactList(contactId); 
                system.debug('conactListController'+conactList);
                Contact contact = (conactList != null && conactList.size() > 0) ? conactList[0] : new Contact();

                List<Campaign> allChildCampaign = new List<Campaign>();
                List<Campaign> childCampaignsToUpdateList = new List<Campaign>();
                List<CampaignMember> campaignMemberList = new List<CampaignMember>();
                List<Task> taskList = new List<Task>();
                List<Database.Saveresult> campaignMemberSaveresultList;

                if(parentCampaignWrapperList2 != null && parentCampaignWrapperList2.size() > 0) {
                    for(ParentCampaignWrapper wrapper : parentCampaignWrapperList2) {
                        if(wrapper != null ) {
                            CampaignMember campaignMember = new CampaignMember(ContactId = contactId, CampaignId= wrapper.childCampaignId); //RecordTypeId = RT_SHEDULED_VOLUNTEER_ID

                            if(campaignMember != null && whyAreYouUnsure != null && whyAreYouUnsure != '') {
                                campaignMember.Why_are_you_unsure__c = whyAreYouUnsure;
                                campaignMemberList.add(campaignMember);
                            }
                            system.debug('***contact.OwnerId***'+contact.OwnerId);
                            Task task = new Task(Subject = 'Unsure-'+ contact.Name , WhoId = contactId,  OwnerId = contact.OwnerId,WhatId = wrapper.childCampaignId);
                            taskList.add(task);
                        }
                    }
                }
                CampaignMember campaignMemberAlreadyPresent;
                if(campaignMemberList != null && campaignMemberList.size() > 0)
                try{
                    VolunteerRenewalUtility.campaignMembersToInsert(campaignMemberList);//Database.insert(campaignMemberList); 
                }catch(System.exception pException) {
                    campaignMemberAlreadyPresent = VolunteerRenewalUtility.campaignMemberAlreadyPresent(campaignMemberList[0].CampaignId, campaignMemberList[0].ContactId); 
                }
                    system.debug('***campaignMemberList***'+campaignMemberList);
                if(taskList <> Null && taskList.size() > 0)
                    VolunteerRenewalUtility.taskList(taskList);
                    //insert taskList; 
                    system.debug('***taskListController***'+taskList);
                if(contact != null) {
                    contact.Get_Involved_Complete__c = true; 
                    update contact;
                }

                String campaignMemberIds = '';
                if(campaignMemberList != null && campaignMemberList.size() > 0) {
                    for(CampaignMember campaignMember : campaignMemberList) {
                        if(campaignMember != null && campaignMember.Id != null)
                            campaignMemberIds = campaignMemberIds == '' ?  string.valueOf(campaignMember.Id) : campaignMemberIds + ',' + string.valueOf(campaignMember.Id);
                    }
                }

                VolunteerRenewalUtility.updateSiteURLAndContact('VolunteerRenewal_MembershipInformation' + '?ContactId='+contactId + '&CouncilId='+councilId+'&CampaignMemberIds'+campaignMemberIds, contact);

                Pagereference JoinMembershipInformationPage = new Pagereference('/apex/VolunteerRenewal_MembershipInformation');
                if(contactId != null && contactId != '')
                    JoinMembershipInformationPage.getParameters().put('ContactId', loggedInContact.Id);
                if(campaignMemberIds != null && contactId != '')
                    JoinMembershipInformationPage.getParameters().put('CampaignMemberIds', campaignMemberIds);
                if(zipCodeCouncilAccount != null)
                    JoinMembershipInformationPage.getParameters().put('CouncilId', zipCodeCouncilAccount.Council__c);
                if(campaignMemberAlreadyPresent != null && campaignMemberAlreadyPresent.Id != null)
                    JoinMembershipInformationPage.getParameters().put('CampaignMemberIds', campaignMemberAlreadyPresent.Id);
                JoinMembershipInformationPage.setRedirect(true);

                return JoinMembershipInformationPage;

                /*
                if(campaignMemberSaveresultList != null) {
                    for (Database.Saveresult sr : campaignMemberSaveresultList) {
                        if (sr.isSuccess()) {

                            VolunteerRenewalUtility.updateSiteURLAndContact('VolunteerRenewal_MembershipInformation' + '?ContactId='+contactId + '&CouncilId='+councilId+'&CampaignMemberIds'+campaignMemberIds, contact);

                            Pagereference JoinMembershipInformationPage = new Pagereference('/apex/VolunteerRenewal_MembershipInformation');
                            if(contactId != null && contactId != '')
                                JoinMembershipInformationPage.getParameters().put('ContactId', loggedInContact.Id);
                            if(campaignMemberIds != null && contactId != '')
                                JoinMembershipInformationPage.getParameters().put('CampaignMemberIds', campaignMemberIds);
                            if(councilId != null && councilId != '')
                                JoinMembershipInformationPage.getParameters().put('CouncilId', councilId);
                            JoinMembershipInformationPage.setRedirect(true);

                            return JoinMembershipInformationPage;
                        }
                        else {
                            // Operation failed, so get all errors                
                            for(Database.Error err : sr.getErrors()) {
                                System.debug(err.getStatusCode() + ': ' + err.getMessage());
                                System.debug('Account fields that affected this error: ' + err.getFields());
                                ApexPages.addMessage(new ApexPages.Message(ApexPages.Severity.WARNING,'Campaign Member Creation Failed'));
                            }
                        }
                    }
                }*/
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
            searchByCampaignName = false;
            /*L ist<Campaign> allChildCampaignWithZipCodeList = GirlRegistrationUtilty.lstCampaign(troopOrGroupName, 'TroopAndZip');
            if(allChildCampaignWithZipCodeList != null && allChildCampaignWithZipCodeList.size() > 0)
                innerParentCampaignWrapperList.addAll(getAllCampaignWithMatchedCriteria(allChildCampaignWithZipCodeList));
            */
            
            Zip_Code__c selectedZipCode = VolunteerRegistrationUtilty.getZipCode(zipCode);
            system.debug('selectedZipCode===>'+selectedZipCode);
            if(selectedZipCode != null && selectedZipCode.geo_location__Latitude__s != null && selectedZipCode.geo_location__Longitude__s != null) {
                system.debug('INside===>');
                List<Zip_Code__c> zipCodeList = VolunteerRegistrationUtilty.getAllZipCodeWithingSelectedRadius(String.valueOf(selectedZipCode.geo_location__Latitude__s), String.valueOf(selectedZipCode.geo_location__Longitude__s), selectedRadius);
                system.debug('zipCodeList======>'+zipCodeList);
                
                Set<String> zipCodeSet = new Set<String>();
                
                for(Zip_Code__c zipCode : zipCodeList) {
                    zipCodeSet.add(zipCode.Zip_Code_Unique__c);
                    system.debug('zipCode.Zip_Code_Unique__c=====>'+zipCode.Zip_Code_Unique__c);
                }
                
                if(!zipCodeSet.isEmpty()) {
                    List<Campaign> allChildCampaignWithZipCodeList = VolunteerRegistrationUtilty.getListOfAllCampaign(troopOrGroupName, 'TroopNameAndZipCode', zipCodeSet);
                    if(allChildCampaignWithZipCodeList != null && allChildCampaignWithZipCodeList.size() > 0)
                        innerParentCampaignWrapperList.addAll(getAllCampaignWithMatchedCriteria(allChildCampaignWithZipCodeList));
                }
            }
            
        }
        else if(troopOrGroupName != null && troopOrGroupName != '') {
            addUnsureCampaign();
  
            //List<Campaign> allCampaignList = VolunteerRenewalUtility.lstCampaign(troopOrGroupName, 'troopName');
            List<Campaign> allCampaignList;
            system.debug('troopOrGroupName#############'+troopOrGroupName);
            
              if(councilId2 !=null && councilId2 !='')
                    {
                         allCampaignList =[
                        Select Parent.Name
                             , ParentId
                             , Parent.Grade__c
                             , Parent.Meeting_Day_s__c
                             , Parent.Meeting_Frequency__c
                             , Parent.Meeting_Location__c
                             , Parent.rC_Volunteers__Required_Volunteer_Count__c
                             , Parent.Display_on_Website__c
                             ,Parent.Troop_Start_Date__c
                             ,Parent.Meeting_Start_Time__c
                             , Parent.Account__c
                             , Id 
                             ,Parent.Participation__c
                             , Name
                             , Council_Code__c 
                             , GS_Volunteers_Required__c 
                          From Campaign 
                         where (Name = :troopOrGroupName OR Parent.Name = :troopOrGroupName)
                           and Display_on_Website__c = true
                           and RecordTypeId = :GirlRegistrationUtilty.getCampaignRecordTypeId(GirlRegistrationUtilty.VOLUNTEER_JOBS_RECORDTYPE)
                           and Parent.Name !='unsure'
                           and Parent.Account__c =:councilId2 
                            ];
                    system.debug('councilId2:'+councilId2);
                    }else{
                           allCampaignList =[
                        Select Parent.Name
                             , ParentId
                             , Parent.Grade__c
                             , Parent.Meeting_Day_s__c
                               , Parent.Meeting_Frequency__c
                             , Parent.Meeting_Location__c
                             , Parent.rC_Volunteers__Required_Volunteer_Count__c
                             , Parent.Display_on_Website__c
                               ,Parent.Troop_Start_Date__c
                                ,Parent.Meeting_Start_Time__c
                             , Parent.Account__c
                             , Id 
                             ,Parent.Participation__c
                             , Name
                             , Council_Code__c 
                             , GS_Volunteers_Required__c 
                          From Campaign 
                         where (Name = :troopOrGroupName OR Parent.Name = :troopOrGroupName)
                           and Display_on_Website__c = true
                           and RecordTypeId = :GirlRegistrationUtilty.getCampaignRecordTypeId(GirlRegistrationUtilty.VOLUNTEER_JOBS_RECORDTYPE)
                           and Parent.Name !='unsure'
                            ];
                    }
            
        system.debug('allCampaignList=====>'+allCampaignList);
            if(allCampaignList != null && allCampaignList.size() > 0) {
                searchByCampaignName = false;
                for(Campaign campaign : allCampaignList)
                    if(campaign != null && campaign.Id != null)
                        innerParentCampaignWrapperList.add(new ParentCampaignWrapper(false, '0', campaign.Name, campaign.Parent.Grade__c, campaign.Parent.Meeting_Location__c,  campaign.Parent.Meeting_Day_s__c, campaign.Parent.Meeting_Start_Date_time__c, String.valueOf(campaign.GS_Volunteers_Required__c), campaign.Parent.Name, campaign.Parent.Account__c, campaign.Id,campaign.Parent.Participation__c));

                if(unsureCampaignRecordList != null && unsureCampaignRecordList.size() > 0) {
                    for(ParentCampaignWrapper wrapper : unsureCampaignRecordList) {
                        if(wrapper != null)
                            innerParentCampaignWrapperList.add(wrapper);
                    }        
                }
            }
            else{
                    searchByCampaignName = true;
                    return innerParentCampaignWrapperList;
            }
        }
        else if(zipCode != null && zipCode != '') {
            searchByCampaignName = false;
            /*List<Campaign> allChildCampaignWithZipCodeList = GirlRegistrationUtilty.lstCampaign(null, 'zip');
            if(allChildCampaignWithZipCodeList != null && allChildCampaignWithZipCodeList.size() > 0)
                innerParentCampaignWrapperList.addAll(getAllCampaignWithMatchedCriteria(allChildCampaignWithZipCodeList));
                */
            
            Zip_Code__c selectedZipCode = VolunteerRegistrationUtilty.getZipCode(zipCode);
            system.debug('selectedZipCode===>'+selectedZipCode);
            if(selectedZipCode != null && selectedZipCode.geo_location__Latitude__s != null && selectedZipCode.geo_location__Longitude__s != null) {
                system.debug('INside===>');
                List<Zip_Code__c> zipCodeList = VolunteerRegistrationUtilty.getAllZipCodeWithingSelectedRadius(String.valueOf(selectedZipCode.geo_location__Latitude__s), String.valueOf(selectedZipCode.geo_location__Longitude__s), selectedRadius);
                system.debug('zipCodeList======>'+zipCodeList);
                
                Set<String> zipCodeSet = new Set<String>();
                
                for(Zip_Code__c zipCode : zipCodeList) {
                    zipCodeSet.add(zipCode.Zip_Code_Unique__c);
                    system.debug('zipCode.Zip_Code_Unique__c=====>'+zipCode.Zip_Code_Unique__c);
                }
                system.debug('zipCodeSet======>'+zipCodeSet);
                
                if(!zipCodeSet.isEmpty()) {
                    List<Campaign> allChildCampaignWithZipCodeList = VolunteerRegistrationUtilty.getListOfAllCampaign('','ZipCode', zipCodeSet);
                    system.debug('allChildCampaignWithZipCodeList===>'+allChildCampaignWithZipCodeList);
                    system.debug('allChildCampaignWithZipCodeList===>'+allChildCampaignWithZipCodeList.size());
                    if(allChildCampaignWithZipCodeList != null && allChildCampaignWithZipCodeList.size() > 0)
                        innerParentCampaignWrapperList.addAll(getAllCampaignWithMatchedCriteria(allChildCampaignWithZipCodeList));
                }
            }
        }

        return innerParentCampaignWrapperList;
    }

    public List<ParentCampaignWrapper> getAllCampaignWithMatchedCriteria(List<Campaign> allChildCampaignWithZipCodeList) {

        List<ParentCampaignWrapper> tempParentCampaignWrapperList = new List<ParentCampaignWrapper>();
        map<Id, String> parentCampaignIdVSDistanceMap = new map<Id, String>();

        addUnsureCampaign();

        if(allChildCampaignWithZipCodeList != null && allChildCampaignWithZipCodeList.size() > 0) {

            parentCampaignIdVSDistanceMap = calculateDistanceForEachCampaign(allChildCampaignWithZipCodeList);

            for(Campaign campaign : allChildCampaignWithZipCodeList) {
                if(campaign != null && campaign.Id != null) {
                    if(parentCampaignIdVSDistanceMap.ContainsKey(campaign.Parent.Id)) {
                        tempParentCampaignWrapperList.add(new ParentCampaignWrapper(false, parentCampaignIdVSDistanceMap.get(campaign.Parent.Id), campaign.Name, campaign.Parent.Grade__c, campaign.Parent.Meeting_Location__c,  campaign.Parent.Meeting_Day_s__c, campaign.Parent.Meeting_Start_Date_time__c, String.valueOf(campaign.GS_Volunteers_Required__c), campaign.Parent.Name, campaign.Parent.Account__c, campaign.Id,campaign.Parent.Participation__c));
                    }
                }
            }
        }

        tempParentCampaignWrapperList.sort();

        if(unsureCampaignRecordList != null && unsureCampaignRecordList.size() > 0) {
            for(ParentCampaignWrapper wrapper : unsureCampaignRecordList)
               if(wrapper != null)
               tempParentCampaignWrapperList.add(wrapper);
        }

        return tempParentCampaignWrapperList;
    }

    public map<Id, String> calculateDistanceForEachCampaign(List<Campaign> allChildCampaignWithZipCodeList) {
        map<Id, List<Campaign>> parentCampaignIdVSChildCampaignListMap = new map<Id, List<Campaign>>();
        set<String> parentCampaignZipCodeSet = new set<String>();
        map<Id, String> parentCampaignIdVSDistanceMap = new map<Id, String>();
        map<String, Id> zipCodeVSParentCampaignIdMap = new map<String, Id>();
        
        Zip_Code__c objZipCode = VolunteerRenewalUtility.getZipCode(zipCode);
        
        if(allChildCampaignWithZipCodeList != null && allChildCampaignWithZipCodeList.size() > 0) {
            
            for(Campaign campaign : allChildCampaignWithZipCodeList) {
                if(campaign != null && campaign.Id != null) {
                    zipCodeVSParentCampaignIdMap.put(campaign.Parent.Zip_Code__c, campaign.Parent.Id);
                    parentCampaignZipCodeSet.add(campaign.Parent.Zip_Code__c);
                }
            }

            if(!parentCampaignZipCodeSet.isEmpty()) {

                List<Zip_Code__c> zipCodeList = VolunteerRenewalUtility.getZipCodeList(parentCampaignZipCodeSet);
                if(zipCodeList != null && zipCodeList.size() > 0) {
                    for(Zip_Code__c zipCodeNew : zipCodeList) {
                        if(zipCodeNew != null && zipCodeNew.geo_location__Latitude__s != null && zipCodeNew.geo_location__Longitude__s != null && objZipCode.geo_location__Latitude__s != null && objZipCode.geo_location__Longitude__s != null) {

                            if(zipCodeVSParentCampaignIdMap.ContainsKey(zipCodeNew.Zip_Code_Unique__c)) {

                                Double latitude1 = Double.valueOf(String.valueOf(zipCodeNew.geo_location__Latitude__s));
                                Double longitude1 = Double.valueOf(String.valueOf(zipCodeNew.geo_location__Longitude__s));

                                Double latitude2 = Double.valueOf(String.valueOf(objZipCode.geo_location__Latitude__s));
                                Double longitude2 = Double.valueOf(String.valueOf(objZipCode.geo_location__Longitude__s));

                                Double distance = calcDistance(latitude1, longitude1, latitude2, longitude2);

                                if( selectedRadius.toUpperCase().contains('NONE') ) {
                                    if(zipCodeVSParentCampaignIdMap.ContainsKey(zipCodeNew.Zip_Code_Unique__c))
                                        parentCampaignIdVSDistanceMap.put(zipCodeVSParentCampaignIdMap.get(zipCodeNew.Zip_Code_Unique__c), String.valueOf(Integer.valueOf(distance)));
                                }

                                else if(Integer.valueOf(distance) <= Integer.valueOf(selectedRadius)) {
                                    if(zipCodeVSParentCampaignIdMap.ContainsKey(zipCodeNew.Zip_Code_Unique__c))
                                        parentCampaignIdVSDistanceMap.put(zipCodeVSParentCampaignIdMap.get(zipCodeNew.Zip_Code_Unique__c), String.valueOf(Integer.valueOf(distance)));
                                }
                            }
                        }
                    }
                }
            }
            return parentCampaignIdVSDistanceMap;
        }
        return null;
    }

  @RemoteAction
    public static List<String> searchCampaingNames(String searchtext1,String councilId22) {

        String JSONString1;
        List<Campaign> campaignList = new List<Campaign>();
        List<String> nameList = new List<String>();

        String searchQueri = 'Select Parent.Account__c,Account__c ,ParentId, Name From Campaign Where Name Like \'%'+searchText1+'%\'  and Display_on_Website__c = true order by Name limit 100' ;
        campaignList = VolunteerRenewalUtility.remoteCampaignList(searchQueri);//database.query(searchQueri);
        system.debug('***campaignList***'+campaignList);
        if(campaignList != null && campaignList.size() > 0) {
            for(Campaign campaign : campaignList)
            {
            if(campaign.Parent.Account__c==councilId22 ||campaign.Account__c==councilId22 )
            nameList.add(campaign.Name);
             }

            JSONString1 = JSON.serialize(nameList);
        }
        return nameList;
    }


    public Contact getContactRecord() {
        Contact contact;
        if(contactId != null) {
            List<Contact> conactList = VolunteerRenewalUtility.contactList(contactId);//[Select Name, Id, MailingPostalCode from Contact where Id = :contactId]; 
            contact = (conactList != null && conactList.size() > 0) ? conactList[0] : new Contact();
        }
        return contact;
    }

    public List<Campaign> getCampaignList() {

        List<Campaign> allCampaignList = [
            Select ParentId
                 , Name
                 , Id
                 , Grade__c
                 , Account__c
                 , Display_on_Website__c
                 , Zip_Code__c
                 , Meeting_Location__c
                 , Meeting_Start_Date_time__c
                 , Meeting_Day_s__c
                 , GS_Volunteers_Required__c 
                 , rC_Volunteers__Required_Volunteer_Count__c ,Parent.Participation__c
              From Campaign
             where Display_on_Website__c = true
               and Zip_Code__c != null
               and RecordTypeId = :RT_VOLUNTEER_JOBS_ID
        ];
        return allCampaignList;
    }
    
    public void addUnsureCampaign() {
        List<Campaign> parentCampaignRecordList = new List<Campaign>();
        unsureCampaignRecordList = new List<ParentCampaignWrapper>();

        parentCampaignRecordList = [
            Select Parent.Name
                 , ParentId
                 , Parent.Grade__c
                 , Parent.Meeting_Day_s__c
                 , Parent.Meeting_Location__c
                 , Parent.rC_Volunteers__Required_Volunteer_Count__c
                 , Parent.Display_on_Website__c
                 , Parent.Meeting_Start_Date_time__c
                 , Parent.Zip_Code__c
                 , Parent.Account__c
                 , Id
                ,Parent.Participation__c
                 , Name
                 , Zip_Code__c
                 , Council_Code__c
                 , GS_Volunteers_Required__c  
              From Campaign 
             where (Parent.Name = 'Unsure' OR Name = 'Unsure')
               and ParentId != null
               and Display_on_Website__c = true
        ];

        if(parentCampaignRecordList != null && parentCampaignRecordList.size() > 0) {
            for(Campaign childCampaign :parentCampaignRecordList)
            if(childCampaign != null)
                unsureCampaignRecordList.add(new ParentCampaignWrapper(false, '0', childCampaign.Name, childCampaign.Parent.Grade__c, childCampaign.Parent.Meeting_Location__c,  childCampaign.Parent.Meeting_Day_s__c, childCampaign.Parent.Meeting_Start_Date_time__c, String.valueOf(childCampaign.GS_Volunteers_Required__c), childCampaign.Parent.Name, childCampaign.Parent.Account__c, childCampaign.Id,childCampaign.Parent.Participation__c));
        }
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
    public class ParentCampaignWrapper implements Comparable{

        public Boolean isCampaignChecked { get; set; }
        public String campaignDistance { get; set; }
        public String childCampaignName { get; set; }
        public String childCampaignId { get; set; }
        public String campaignGrade { get; set; }
        public String campaignMeetingLocation { get; set; }
        public String campaignMeetingDay { get; set; }
        public DateTime campaignMeetingStartDatetime { get; set; }
        public String campaignVolunteersRequired { get; set; }
        public String parentCampaignName { get; set; }
        public String campaignMeetingStartDatetimeStd { get; set; }
        public Id campaignId { get; set; }
        public String parentCampaignAccountId;
         public String campaignParticipationType { get; set; }
        public ParentCampaignWrapper(Boolean campaignChecked, String strCampaignDistance, String strChildCampaignName, String strCampaignGrade, String strCampaignMeetingLocation, String strcampaignMeetingDay, DateTime strCampaignMeetingStartDatetime, String strCampaignVolunteersRequired, String strParentCampaignName, String strAccountId, String strChildCampaignId,String strCampaignParticipation) {
            isCampaignChecked = campaignChecked;
            campaignDistance = strCampaignDistance;
            childCampaignName = strChildCampaignName;
            campaignGrade = strCampaignGrade;
            campaignMeetingLocation = strCampaignMeetingLocation;
            campaignMeetingDay = strcampaignMeetingDay;
            campaignMeetingStartDatetime = strCampaignMeetingStartDatetime;
            campaignVolunteersRequired = strCampaignVolunteersRequired;
            parentCampaignName = strParentCampaignName;
            parentCampaignAccountId = strAccountId;
            childCampaignId = strChildCampaignId;
              campaignId = strChildCampaignId;
              campaignParticipationType = strCampaignParticipation;
            if(strCampaignMeetingStartDatetime!=null)
                campaignMeetingStartDatetimeStd = String.valueOf(strCampaignMeetingStartDatetime.getTime());
        }

        public Integer compareTo(Object compareTo) {
            ParentCampaignWrapper compareToParentCamp = (ParentCampaignWrapper)compareTo;
            if (Integer.valueOf(campaignDistance) == Integer.valueOf(compareToParentCamp.campaignDistance)) return 0;
            if (Integer.valueOf(campaignDistance) > Integer.valueOf(compareToParentCamp.campaignDistance)) return 1;
            return -1;

            return null;
        }
    }
}