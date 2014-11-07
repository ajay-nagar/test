public with sharing class Volunteer_TroopGroupRoleSearchController extends SobjectExtension {

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
    public boolean searchByCampaignName ;
    public static Boolean isCouncilHeaderAvailable { get; set; }
    
    //change
    //public PageReference currentURL ;
    public String CurrentURl ;

     public Boolean showselectedcampaign { get; set; }
     public Boolean isunsure { get; set; }
     public ID deleteselectedrecordid { get; set; }
     public ID selectedcampaignidd {get; set; }
     public List<ParentCampaignWrapper> parentCampaignWrapperList2 { get; set;}
    public List<ParentCampaignWrapper> parentCampaignWrapperList { get; set;}
    public Boolean showSearchResultTable {get; set;}
    public List<Integer> pageNumberSet {get; set;}
    public String selectedPageSize {get; set;}
    public String selectedPageNumber {get; set;}
    public static final Map<String, Schema.FieldSet> FIELDSETS_CAMPAIGN = SObjectType.Campaign.FieldSets.getMap();

    private String contactId;
    public String councilId { get; set; }
    private static Integer counterUnableToLockRow = 0;

    private List<ParentCampaignWrapper> unsureCampaignRecordList;

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

    private static String getCampaignRecordTypeId(String name) {
       return (CAMPAIGN_RECORDTYPE_INFO_MAP.get(name) != null) ? CAMPAIGN_RECORDTYPE_INFO_MAP.get(name).getRecordTypeId() : null;
    }

    private static String getContactRecordTypeId(String name) {
       return (CONTACT_RECORDTYPE_INFO_MAP.get(name) != null) ? CONTACT_RECORDTYPE_INFO_MAP.get(name).getRecordTypeId() : null;
    }
    
    private static String getAccountRecordTypeId(String name) {
       return (ACCOUNT_RECORDTYPE_INFO_MAP.get(name) != null) ? ACCOUNT_RECORDTYPE_INFO_MAP.get(name).getRecordTypeId() : null;
    }
    
  
    

    public Volunteer_TroopGroupRoleSearchController() {
        counterUnableToLockRow = 0;
        selectedPageSize = '10';
        showSearchResultTable = false;
        whyAreYouUnsure = '';
        pagerFlag = false;
        searchByCampaignName = false;
        isCouncilHeaderAvailable = false;
            showselectedcampaign =false;
        //change start
        
        CurrentURL= ApexPages.currentPage().getURL();
        system.debug('---Current Url -->'+CurrentURL);
        
        //change End
        parentCampaignWrapperList2 = new List<ParentCampaignWrapper>();
        pageNumberSet = new List<Integer>();
        parentCampaignWrapperList = new List<ParentCampaignWrapper>();
        unsureCampaignRecordList = new List<ParentCampaignWrapper>();

        if (Apexpages.currentPage().getParameters().containsKey('ContactId'))
            contactId = Apexpages.currentPage().getParameters().get('ContactId');

        if (Apexpages.currentPage().getParameters().containsKey('CouncilId'))
            councilId = Apexpages.currentPage().getParameters().get('CouncilId');

        system.debug('councilId==>'+councilId);
        if(councilId != null && councilId != ''){
            VolunteerController.councilAccount = VolunteerRegistrationUtilty.getCouncilAccount(councilId);
         if (VolunteerController.councilAccount.Council_Header_Url__c != null && VolunteerController.councilAccount.Council_Header_Url__c != '') {
                    isCouncilHeaderAvailable = true;
                } 
        }
        Contact objCcontact = getContactRecord();
        if(objCcontact != null && objCcontact.Id != null)
            zipCode = objCcontact.MailingPostalCode;
        system.debug('addUnsureCampaign 1--->');
        addUnsureCampaign();
    }
    
  /*  public PageReference currentURL() {
      
    }*/

    public PageReference searchTroopORGroupRoleByNameORZip() {
        Savepoint savepoint = Database.setSavepoint();

        system.debug('councilId11==>'+councilId);
        try {
            pagerFlag = true;
            if(councilId != null && councilId != '') {
                VolunteerController.councilAccount = VolunteerRegistrationUtilty.getCouncilAccount(councilId);

                if (VolunteerController.councilAccount != null && VolunteerController.councilAccount.Council_Header_Url__c != null && VolunteerController.councilAccount.Council_Header_Url__c != '') {
                    isCouncilHeaderAvailable = true;
                }
            }
            List<ParentCampaignWrapper> newTempWrapperList = new List<ParentCampaignWrapper>();
            newTempWrapperList = obtainParentCampaignWrapperList();
            
            system.debug('==================>'+newTempWrapperList);
            if(searchByCampaignName == true) {
                //system.debug('######11111###'+newTempWrapperList);
                if(newTempWrapperList.size() == 0)
                    parentCampaignWrapperList.clear();
                       List<Campaign> unsureCampaignList = [
            Select Parent.Name
                 , ParentId
                 , Parent.Grade__c
                 , Parent.Meeting_Day_s__c
                 , Parent.Meeting_Frequency__c
                 , Parent.Meeting_Location__c
                 , Parent.rC_Volunteers__Required_Volunteer_Count__c
                 , Parent.Display_on_Website__c
                 , Parent.Troop_Start_Date__c
                 , Parent.Meeting_Start_Time__c
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
 parentCampaignWrapperList.add(new ParentCampaignWrapper(false, '0', campaign.Name, campaign.Parent.Grade__c, campaign.Parent.Meeting_Location__c,  campaign.Parent.Meeting_Day_s__c,  campaign.Parent.Meeting_Frequency__c ,  campaign.Parent.Troop_Start_Date__c
                 , campaign.Parent.Meeting_Start_Time__c, String.valueOf(campaign.Volunteer_Openings_Remaining__c), campaign.Parent.Name, campaign.Parent.Account__c, campaign.Id,campaign.Parent.Participation__c));
}              if(troopOrGroupName!='unsure' ){
                return addErrorMessageAndRollback(savepoint,'No Troop/Groups with this Name Exists.');
            }else{
            return  null;   }
            
           } 
            if(newTempWrapperList.size() == 0) {
                system.debug('######222###');
                parentCampaignWrapperList.clear();
                pagerFlag = false;
                return addErrorMessageAndRollback(savepoint,'No Troop/Group found for this Zip code within the radius selected');
            }

            if(newTempWrapperList != null && newTempWrapperList.size() > 0) {

                fillRolesToDisplayPerPage(newTempWrapperList.size());

                parentCampaignWrapperList.clear();

                for(Integer recordSize = 0; recordSize < Integer.valueOf(selectedPageSize);  recordSize++) {
                    if(newTempWrapperList.size() > recordSize)
                        parentCampaignWrapperList.add(newTempWrapperList[recordSize]);
                }

                if(parentCampaignWrapperList == null || parentCampaignWrapperList.size() == 0) {
                    system.debug('######3333###');
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
        try{
            campaignPopup = new Campaign();
            Campaign[] campaignList = (Campaign[]) Database.query(''
            + 'SELECT ' + generateFieldSelect(selectedFields)
            + ', ParentId, Parent.Grade__c'
            + '  FROM Campaign'
            + ' WHERE Id = '+'\''+campaignDetailsId+'\''
            + '   AND Display_on_Website__c = true'
        );
            campaignPopup = campaignList.size() > 0 ? campaignList[0] : null;
            if(campaignPopup != null && campaignPopup.Parent != null && campaignPopup.Parent.Grade__c != null)
                campaignPopup.Grade__c = campaignPopup.Parent.Grade__c;
        } catch(System.exception pException) {
            return addErrorMessageAndRollback(savepoint, pException);
        }

        return null;
    }

    /*public PageReference showDetails() {
        Savepoint savepoint = Database.setSavepoint();

        try {
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
                     , Description_Detail__c
                     , rC_Volunteers__Required_Volunteer_Count__c 
                     , GS_Volunteers_Required__c 
                     , Volunteer_Openings_Remaining__c
                  From Campaign 
                 where Id =: campaignDetailsId
                   and Display_on_Website__c = true
            ];

            campaignPopup = (campaignList.size() > 0 && campaignList != null) ? campaignList[0] : null;

            if(campaignPopup != null && campaignPopup.Parent != null && campaignPopup.Parent.Grade__c != null)
                campaignPopup.Grade__c = campaignPopup.Parent.Grade__c;
        } catch(System.exception pException) {
            return addErrorMessageAndRollback(savepoint, pException);
        }        
        return null;
    }*/

    public PageReference clearSelections() {
        Savepoint savepoint = Database.setSavepoint();

        try {
            pagerFlag = false;
             parentCampaignWrapperList2.clear();
            parentCampaignWrapperList.clear();
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
        system.debug('==== counterUnableToLockRow : ' + counterUnableToLockRow);
        Savepoint savepoint = Database.setSavepoint();

        try {

            if(contactId != null && contactId != '') {

                Contact contact = getContactRecord();

                List<Campaign> allChildCampaign = new List<Campaign>();
                List<Campaign> childCampaignsToUpdateList = new List<Campaign>();
                List<CampaignMember> campaignMemberList = new List<CampaignMember>();

                Set<Id> campaignIdSet = new Set<Id>();
                Set<Id> campaignMemberIdSet = new Set<Id>();
                map<String, CampaignMember> contactIdCampaignId_CampaignMemberMap = new map<String, CampaignMember>();
                /**************Add on 30-10-2014********************/
                Boolean isPrimaryCampaignMember = false;
                Boolean isPrimaryCampaignMemberForSelection = false;
                Set<Id> allVolunteerJobCampaignSet = new Set<Id>();
                map<ID,ID> AlreadyExistCampaignMemberMap = new map<ID,ID>();
                List<Campaign> allVolunteerJobCampaignList = VolunteerRenewalUtility.getAllVolunteerJobCampaign();
                if(allVolunteerJobCampaignList!= null && allVolunteerJobCampaignList.size() > 0) {

                    for(Campaign campaign :allVolunteerJobCampaignList)
                        allVolunteerJobCampaignSet.add(campaign.Id);

                    //if this contact is primary campaignMember to other campaign then attchg this as campaignMember
                    List<CampaignMember> campaignMemberForCurrentAdultList = [
                        Select Primary__c
                             , Active__c
                             , ContactId
                             , CampaignId 
                          From CampaignMember
                         where CampaignId= :allVolunteerJobCampaignSet
                           and ContactId = :contactId
                    ];

                    if(campaignMemberForCurrentAdultList!= null && campaignMemberForCurrentAdultList.size() > 0) {
                         for(CampaignMember campaignMember :campaignMemberForCurrentAdultList) {
                            if(campaignMember.Primary__c && campaignMember.Active__c)
                                isPrimaryCampaignMember = true;
                            AlreadyExistCampaignMemberMap.put(campaignMember.CampaignId,campaignMember.ContactId);
                            system.debug('Campaign Id==>' + campaignMember.CampaignId +'Contact Id==>'+campaignMember.ContactId); 
                        }
                    }
                }

                
                
                /**************Add on 30-10-2014********************/
                String campaignMemberIds = '';
                if(parentCampaignWrapperList2 !=  null && parentCampaignWrapperList2.size() > 0) {
                    for(ParentCampaignWrapper wrapper : parentCampaignWrapperList2) {
                     
                            campaignIdSet.add(wrapper.childCampaignId);

                            if(wrapper.parentCampaignName != null && wrapper.parentCampaignName != '') {
                                if(wrapper.parentCampaignAccountId != null) {
                                    /*Change on 30-10-2014*/
                                    /*CampaignMember campaignMember = new CampaignMember(ContactId = contactId, CampaignId= wrapper.childCampaignId, Account__c = wrapper.parentCampaignAccountId); //RecordTypeId = RT_SHEDULED_VOLUNTEER_ID
                                    if(campaignMember != null)
                                        campaignMemberList.add(campaignMember);*/
                                        
                                        
                                
                                    if(!isPrimaryCampaignMember && !isPrimaryCampaignMemberForSelection ) {
                                       if(!AlreadyExistCampaignMemberMap.containsKey(wrapper.childCampaignId)){
                                        CampaignMember campaignMember = new CampaignMember(ContactId = contactId, CampaignId= wrapper.childCampaignId, Account__c = wrapper.parentCampaignAccountId, Primary__c = true, Active__c = false);
                                        if(campaignMember != null) {
                                        campaignMemberList.add(campaignMember);
                                        isPrimaryCampaignMemberForSelection = true;
                                         }
                                       }
                                    }
                                    else {
                                        CampaignMember campaignMember = new CampaignMember(ContactId = contactId, CampaignId= wrapper.childCampaignId, Account__c = wrapper.parentCampaignAccountId, Primary__c = false, Active__c = false);
                                        campaignMemberList.add(campaignMember);
                                    }
                                
                                
                                
                                
                                
                                }
                                else {
                                     return addErrorMessageAndRollback(savepoint,'Please contact the council for help with the \'' + wrapper.parentCampaignName + '\' role. Thank You.');
                                }
                            }
                            else {
                                 return addErrorMessageAndRollback(savepoint,'Please contact the council for help. Selected Troop doesn\'t have a Parent.');
                            }
                        
                    }
                }

                List<CampaignMember> newCampaignMemberList = new List<CampaignMember>();

                List<CampaignMember> existingCampaignMemberList = [
                    Select Id
                         , contactId
                         , CampaignId
                      from CampaignMember 
                     where ContactId = :contactId 
                       and CampaignId IN :campaignIdSet
                ];

                system.debug('********* existingCampaignMemberList'+existingCampaignMemberList);
                if(existingCampaignMemberList != null && existingCampaignMemberList.size() > 0) {
                    for(CampaignMember campaignMember : existingCampaignMemberList) {
                        if(campaignMember != null && campaignMember.contactId != null && campaignMember.CampaignId != null) {
                            String contactIdCampaignIdString = campaignMember.contactId + '' + campaignMember.CampaignId;
                            contactIdCampaignId_CampaignMemberMap.put(contactIdCampaignIdString, campaignMember);
                        }
                    }
                }
                system.debug('********* campaignMemberList'+campaignMemberList);
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
                }

                List<Database.Saveresult> campaignMemberSaveresultList;
                if(newCampaignMemberList != null && newCampaignMemberList.size() > 0)
                    campaignMemberSaveresultList = Database.insert(newCampaignMemberList);
                system.debug('1. campaignMemberSaveresultList ===: ' + campaignMemberSaveresultList);
                if(contact != null) {
                    contact.Get_Involved_Complete__c = true;
                    update contact;
                }

                if(campaignMemberIdSet != null && campaignMemberIdSet.size() > 0) {
                    for(Id campaignMemberId : campaignMemberIdSet) {
                        campaignMemberIds = campaignMemberIds == '' ?  string.valueOf(campaignMemberId) : campaignMemberIds + ',' + string.valueOf(campaignMemberId);
                    }
                }

                if(newCampaignMemberList != null && newCampaignMemberList.size() > 0) {
                    for(CampaignMember campaignMember : newCampaignMemberList) {
                        if(campaignMember != null)
                            campaignMemberIds = campaignMemberIds == '' ?  string.valueOf(campaignMember.Id) : campaignMemberIds + ',' + string.valueOf(campaignMember.Id);
                    }
                }

                system.debug('2. campaignMemberSaveresultList ===: ' + campaignMemberSaveresultList);
                if(campaignMemberSaveresultList != null) {

                    for (Database.Saveresult sr : campaignMemberSaveresultList) {
                        system.debug('SR ===: ' + sr);
                        if (sr.isSuccess()) {

                            //VolunteerRegistrationUtilty.updateSiteURLAndContact('Volunteer_JoinMembershipInformation' + '?ContactId='+contactId + '&CouncilId='+councilId+'&CampaignMemberIds'+campaignMemberIds, contact);
                            VolunteerRegistrationUtilty.updateSiteURLAndContact('Volunteer_JoinMembershipInformation' + '?ContactId='+contactId + '&CouncilId='+councilId+'&CampaignMemberIds='+campaignMemberIds, contact);

                            Pagereference JoinMembershipInformationPage = Page.Volunteer_JoinMembershipInformation;
                            if(contactId != null && contactId != '')
                                JoinMembershipInformationPage.getParameters().put('ContactId', contactId);
                            if(campaignMemberIds != null && contactId != '')
                                JoinMembershipInformationPage.getParameters().put('CampaignMemberIds', campaignMemberIds);
                            if(councilId != null && councilId != '')
                                JoinMembershipInformationPage.getParameters().put('CouncilId', councilId);
                            system.debug('1. JoinMembershipInformationPage ===: ' + JoinMembershipInformationPage);
                            JoinMembershipInformationPage.setRedirect(true);

                            system.debug('2. JoinMembershipInformationPage ===: ' + JoinMembershipInformationPage);
                            return JoinMembershipInformationPage;
                        }
                        else {
                            // Operation failed, so get all errors                
                            for(Database.Error err : sr.getErrors()) {
                                System.debug('Campaign Member Errors :  ' + err.getStatusCode() + ': ' + err.getMessage());
                                 return addErrorMessageAndRollback(savepoint,'Campaign Member Creation Failed');
                            }
                        }
                    }
                }

                if(campaignMemberIds != null && campaignMemberIds != '') {
                    Pagereference JoinMembershipInformationPage = Page.Volunteer_JoinMembershipInformation;//new Pagereference('/apex/Volunteer_JoinMembershipInformation');
                    if(contactId != null && contactId != '')
                        JoinMembershipInformationPage.getParameters().put('ContactId', contactId);
                    if(campaignMemberIds != null && contactId != '')
                        JoinMembershipInformationPage.getParameters().put('CampaignMemberIds', campaignMemberIds);
                    if(councilId != null && councilId != '')
                        JoinMembershipInformationPage.getParameters().put('CouncilId', councilId);
                    JoinMembershipInformationPage.setRedirect(true);
                    return JoinMembershipInformationPage;
                }
            }
            else{
                return addErrorMessageAndRollback(savepoint,'Please specify Contact');
            }
        } catch(System.exception pException) {
            system.debug('Exception ====:  ' + pException.getMessage());
            if(pException.getMessage().contains('UNABLE_TO_LOCK_ROW')){
                system.debug('counterUnableToLockRow ====:  ' + counterUnableToLockRow);
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
                List<Contact> conactList = [Select Name, OwnerId, Id from Contact where Id = :contactId]; 
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
                            Task task = new Task(Subject = 'Unsure-'+ contact.Name , WhoId = contactId,  OwnerId = contact.OwnerId,WhatId = wrapper.childCampaignId);
                            taskList.add(task);
                        }
                    }
                }
                CampaignMember campaignMemberAlreadyPresent;
                if(campaignMemberList != null && campaignMemberList.size() > 0) {
                    try{
                        campaignMemberSaveresultList= Database.insert(campaignMemberList);
                    }catch(System.exception pException) {
                         campaignMemberAlreadyPresent = VolunteerRegistrationUtilty.campaignMemberAlreadyPresent(campaignMemberList[0].CampaignId, campaignMemberList[0].ContactId);
                    }
                }
                if(taskList <> Null && taskList.size() > 0)
                    insert taskList; 
                if(contact != null) {
                    contact.Get_Involved_Complete__c = true;
                    update contact;
                }
                String campaignMemberIds = '';
                system.debug('***campaignMemberList***'+campaignMemberList);
                if(campaignMemberList != null && campaignMemberList.size() > 0) {
                    for(CampaignMember campaignMember : campaignMemberList) {
                        if(campaignMember != null)
                            campaignMemberIds = campaignMemberIds == '' ?  string.valueOf(campaignMember.Id) : campaignMemberIds + ',' + string.valueOf(campaignMember.Id);
                    }
                }
                if(campaignMemberSaveresultList != null) {
                    for (Database.Saveresult sr : campaignMemberSaveresultList) {
                        if (sr.isSuccess()) {
                            //VolunteerRegistrationUtilty.updateSiteURLAndContact('Volunteer_JoinMembershipInformation' + '?ContactId='+contactId + '&CouncilId='+councilId+'&CampaignMemberIds'+campaignMemberIds, contact);
                            VolunteerRegistrationUtilty.updateSiteURLAndContact('Volunteer_JoinMembershipInformation' + '?ContactId='+contactId + '&CouncilId='+councilId+'&CampaignMemberIds='+campaignMemberIds, contact);
                            Pagereference JoinMembershipInformationPage = Page.Volunteer_JoinMembershipInformation;//new Pagereference('/apex/Volunteer_JoinMembershipInformation');
                            if(contactId != null && contactId != '')
                                JoinMembershipInformationPage.getParameters().put('ContactId', contactId);
                            if(campaignMemberIds != null && contactId != '')
                                JoinMembershipInformationPage.getParameters().put('CampaignMemberIds', campaignMemberIds);
                            if(councilId != null && councilId != '')
                                JoinMembershipInformationPage.getParameters().put('CouncilId', councilId);
                            JoinMembershipInformationPage.setRedirect(true);

                            return JoinMembershipInformationPage;
                        }
                        else {
                            for(Database.Error err : sr.getErrors()) {
                                System.debug(err.getStatusCode() + ': ' + err.getMessage());
                                System.debug('Account fields that affected this error: ' + err.getFields());
                                 return addErrorMessageAndRollback(savepoint,'Campaign Member Creation Failed');
                            }
                        }
                    }
                }else {
                    //VolunteerRegistrationUtilty.updateSiteURLAndContact('Volunteer_JoinMembershipInformation' + '?ContactId='+contactId + '&CouncilId='+councilId+'&CampaignMemberIds'+campaignMemberIds, contact);
                    VolunteerRegistrationUtilty.updateSiteURLAndContact('Volunteer_JoinMembershipInformation' + '?ContactId='+contactId + '&CouncilId='+councilId+'&CampaignMemberIds='+campaignMemberIds, contact);
                            Pagereference JoinMembershipInformationPage = Page.Volunteer_JoinMembershipInformation;//new Pagereference('/apex/Volunteer_JoinMembershipInformation');
                            if(contactId != null && contactId != '')
                                JoinMembershipInformationPage.getParameters().put('ContactId', contactId);
                            if(campaignMemberAlreadyPresent != null && campaignMemberAlreadyPresent.Id != null)
                                JoinMembershipInformationPage.getParameters().put('CampaignMemberIds', campaignMemberAlreadyPresent.Id);
                            if(councilId != null && councilId != '')
                                JoinMembershipInformationPage.getParameters().put('CouncilId', councilId);
                            JoinMembershipInformationPage.setRedirect(true);

                            return JoinMembershipInformationPage;
                }
            }
        } catch(System.exception pException) {
             system.debug('**error***');
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
            /*List<Campaign> allChildCampaignWithZipCodeList = VolunteerRegistrationUtilty.getListOfAllCampaign(troopOrGroupName,'TroopNameAndZipCode');
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
            
                 List<Campaign> allCampaignList;
              if(councilId !=null && councilId !='')
                    {
                         allCampaignList =[
                        Select Parent.Name
                             ,Volunteer_Openings_Remaining__c
                             , ParentId
                             , Parent.Grade__c
                             , Parent.Meeting_Day_s__c
                              , Parent.Meeting_Frequency__c
                             , Parent.Meeting_Location__c
                             , Parent.rC_Volunteers__Required_Volunteer_Count__c
                             , Parent.Display_on_Website__c
                             , Parent.Meeting_Start_Time__c
                             , Parent.Troop_Start_Date__c
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
                           and Parent.Account__c =:councilId 
                            ];
                    
                    }else{
                         allCampaignList = VolunteerRegistrationUtilty.getListOfAllCampaign(troopOrGroupName,'TroopName', new Set<String>());
                        }
            if(allCampaignList != null && allCampaignList.size() > 0) {
                searchByCampaignName = false;
                for(Campaign campaign : allCampaignList)
                    if(campaign != null && campaign.Id != null)
                        innerParentCampaignWrapperList.add(new ParentCampaignWrapper(false, '0', campaign.Name, campaign.Parent.Grade__c, campaign.Parent.Meeting_Location__c,  campaign.Parent.Meeting_Day_s__c , campaign.Parent.Meeting_Frequency__c, campaign.Parent.Troop_Start_Date__c
                 , campaign.Parent.Meeting_Start_Time__c, String.valueOf(campaign.Volunteer_Openings_Remaining__c), campaign.Parent.Name, campaign.Parent.Account__c, campaign.Id,campaign.Parent.Participation__c));

                if(unsureCampaignRecordList != null && unsureCampaignRecordList.size() > 0) {
                    for(ParentCampaignWrapper wrapper : unsureCampaignRecordList) {
                        if(wrapper != null)
                            innerParentCampaignWrapperList.add(wrapper);
                    }        
                }
            }
            else {
                searchByCampaignName = true;
                return innerParentCampaignWrapperList;
            }
        }
        else if(zipCode != null && zipCode != '') {
            searchByCampaignName = false;
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
            
            /*List<Campaign> allChildCampaignWithZipCodeList = VolunteerRegistrationUtilty.getListOfAllCampaign(troopOrGroupName,'ZipCode');
            if(allChildCampaignWithZipCodeList != null && allChildCampaignWithZipCodeList.size() > 0)
                innerParentCampaignWrapperList.addAll(getAllCampaignWithMatchedCriteria(allChildCampaignWithZipCodeList));*/
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
                        tempParentCampaignWrapperList.add(new ParentCampaignWrapper(false, parentCampaignIdVSDistanceMap.get(campaign.Parent.Id), campaign.Name, campaign.Parent.Grade__c, campaign.Parent.Meeting_Location__c,  campaign.Parent.Meeting_Day_s__c , campaign.Parent.Meeting_Frequency__c,  campaign.Parent.Troop_Start_Date__c
                 , campaign.Parent.Meeting_Start_Time__c, String.valueOf(campaign.Volunteer_Openings_Remaining__c), campaign.Parent.Name, campaign.Parent.Account__c, campaign.Id,campaign.Parent.Participation__c));
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
        map<String, Set<Id>> zipCodeVSParentCampaignIdMap = new map<String, Set<Id>>();

        Zip_Code__c objZipCode = VolunteerRegistrationUtilty.getZipCode(zipCode);

        if(allChildCampaignWithZipCodeList != null && allChildCampaignWithZipCodeList.size() > 0) {
            
            for(Campaign campaign : allChildCampaignWithZipCodeList) {
                if(campaign != null && campaign.Id != null) {
                    //zipCodeVSParentCampaignIdMap.put(campaign.Parent.Zip_Code__c, campaign.Parent.Id);

                    if(zipCodeVSParentCampaignIdMap.containsKey(campaign.Parent.Zip_Code__c)) {
                        Set<Id> campaignIdSet = zipCodeVSParentCampaignIdMap.get(campaign.Parent.Zip_Code__c);
                        campaignIdSet.add(campaign.Parent.Id);
                        zipCodeVSParentCampaignIdMap.put(campaign.Parent.Zip_Code__c, campaignIdSet);
                    }
                    else {
                        Set<Id> campaignIdSet = new Set<Id>();
                        campaignIdSet.add(campaign.Parent.Id);
                        zipCodeVSParentCampaignIdMap.put(campaign.Parent.Zip_Code__c, campaignIdSet);
                    }

                    parentCampaignZipCodeSet.add(campaign.Parent.Zip_Code__c);
                }
            }

            if(!parentCampaignZipCodeSet.isEmpty()) {

                List<Zip_Code__c> zipCodeList = VolunteerRegistrationUtilty.getZipCodeList(parentCampaignZipCodeSet);
                if(zipCodeList != null && zipCodeList.size() > 0) {
                    for(Zip_Code__c zipCodeNew : zipCodeList) {
                        if(zipCodeNew != null && zipCodeNew.geo_location__Latitude__s != null && zipCodeNew.geo_location__Longitude__s != null && objZipCode.geo_location__Latitude__s != null && objZipCode.geo_location__Longitude__s != null) {

                            if(zipCodeVSParentCampaignIdMap != null && !zipCodeVSParentCampaignIdMap.isEmpty() && zipCodeVSParentCampaignIdMap.ContainsKey(zipCodeNew.Zip_Code_Unique__c)) {

                                Double latitude1 = Double.valueOf(String.valueOf(zipCodeNew.geo_location__Latitude__s));
                                Double longitude1 = Double.valueOf(String.valueOf(zipCodeNew.geo_location__Longitude__s));

                                Double latitude2 = Double.valueOf(String.valueOf(objZipCode.geo_location__Latitude__s));
                                Double longitude2 = Double.valueOf(String.valueOf(objZipCode.geo_location__Longitude__s));

                                Double distance = calcDistance(latitude1, longitude1, latitude2, longitude2);

                                if( selectedRadius.toUpperCase().contains('NONE') ) {
                                    Set<Id> campaignIdSet = zipCodeVSParentCampaignIdMap.get(zipCodeNew.Zip_Code_Unique__c);
                                    for(Id campaignId : campaignIdSet)
                                        parentCampaignIdVSDistanceMap.put(campaignId, String.valueOf(Integer.valueOf(distance)));
                                }

                                else if(Integer.valueOf(distance) <= Integer.valueOf(selectedRadius)) {
                                    Set<Id> campaignIdSet = zipCodeVSParentCampaignIdMap.get(zipCodeNew.Zip_Code_Unique__c);
                                    for(Id campaignId : campaignIdSet)
                                        parentCampaignIdVSDistanceMap.put(campaignId, String.valueOf(Integer.valueOf(distance)));
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
    public static List<String> searchCampaingNames(String searchtext1,String councilId2 ) {
        String JSONString1;
        List<Campaign> campaignList = new List<Campaign>();
        List<String> nameList = new List<String>();
        String searchQueri='';

       // searchQueri = 'Select ParentId, Name From Campaign Where Name Like \'%'+searchText1+'%\' and (Parent.Account__c ='+'\'' +councilId+ '\' or Account__c ='+ councilId+')  and Display_on_Website__c = true order by Name limit 100' ;
             searchQueri = 'Select Parent.Account__c,Account__c, ParentId, Name From Campaign Where Name Like \'%'+searchText1+'%\'  and Display_on_Website__c = true order by Name limit 100' ;
               campaignList = database.query(searchQueri);
           
            

        if(campaignList != null && campaignList.size() > 0) {
            for(Campaign campaign : campaignList)
            {
            if(campaign.Parent.Account__c==councilId2 ||campaign.Account__c==councilId2 )
            nameList.add(campaign.Name);
             }
            JSONString1 = JSON.serialize(nameList);
        }
        return nameList;
    }

    public Contact getContactRecord() {
        Contact contact;
        if(contactId != null) {
            List<Contact> conactList = [Select Name, Id, MailingPostalCode from Contact where Id = :contactId]; 
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
                 ,Troop_Start_Date__c
                 ,Meeting_Start_Time__c
                 , Meeting_Day_s__c
                  ,Meeting_Frequency__c
                 , GS_Volunteers_Required__c 
                 , rC_Volunteers__Required_Volunteer_Count__c,Parent.Participation__c
                 , Volunteer_Openings_Remaining__c
              From Campaign
             where Display_on_Website__c = true
               and Parent.Name != 'Unsure'
               and Name != 'Unsure'
               and Zip_Code__c != null
               and RecordTypeId = :RT_VOLUNTEER_JOBS_ID
        ];
        return allCampaignList;
    }
    
    public void addUnsureCampaign() {
        List<Campaign> parentCampaignRecordList = new List<Campaign>();
        unsureCampaignRecordList = new List<ParentCampaignWrapper>();
        system.debug('addUnsureCampaign 1--->');
        system.debug('parentCampaignRecordList--->'+parentCampaignRecordList);
        parentCampaignRecordList = [
            Select Parent.Name
                 , ParentId
                 , Parent.Grade__c
                 , Parent.Meeting_Day_s__c
                  , Parent.Meeting_Frequency__c
                 , Parent.Meeting_Location__c
                 , Parent.rC_Volunteers__Required_Volunteer_Count__c
                 , Parent.Display_on_Website__c
                 , Parent.Troop_Start_Date__c
                 , Parent.Meeting_Start_Time__c
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
        system.debug('parentCampaignRecordList--->'+parentCampaignRecordList);
        if(parentCampaignRecordList != null && parentCampaignRecordList.size() > 0) {
            for(Campaign childCampaign :parentCampaignRecordList)
            if(childCampaign != null)
                unsureCampaignRecordList.add(new ParentCampaignWrapper(false, '0', childCampaign.Name, childCampaign.Parent.Grade__c, childCampaign.Parent.Meeting_Location__c,  childCampaign.Parent.Meeting_Day_s__c , childCampaign.Parent.Meeting_Frequency__c
, childCampaign.Parent.Troop_Start_Date__c, childCampaign.Parent.Meeting_Start_Time__c, String.valueOf(childCampaign.Volunteer_Openings_Remaining__c), childCampaign.Parent.Name, childCampaign.Parent.Account__c, childCampaign.Id,childCampaign.Parent.Participation__c));
        }
    }

    public class ParentCampaignWrapper implements Comparable{

        public Boolean isCampaignChecked { get; set; }
        public String campaignDistance { get; set; }
        public String childCampaignName { get; set; }
        public String childCampaignId { get; set; }
        public String campaignGrade { get; set; }
        public String campaignMeetingLocation { get; set; }
        public String campaignMeetingDay { get; set; }
         public String campaignMeetingDayfrequency { get; set; }
        public DateTime campaignMeetingStartDatetime { get; set; }
        public String campaignVolunteersRequired { get; set; }
        public String parentCampaignName { get; set; }
        public String campaignMeetingStartDatetimeStd { get; set; }
         public Id campaignId { get; set; }
        public String parentCampaignAccountId;
        public String campaignParticipationType { get; set; }
        public ParentCampaignWrapper(Boolean campaignChecked, String strCampaignDistance, String strChildCampaignName, String strCampaignGrade, String strCampaignMeetingLocation, String strcampaignMeetingDay, String strcampaignMeetingDayfrequency, Date TroopStartDate,String MeetingStartTime, String strCampaignVolunteersRequired, String strParentCampaignName, String strAccountId, String strChildCampaignId,String strCampaignParticipation) {
            isCampaignChecked = campaignChecked;
            campaignDistance = strCampaignDistance;
            childCampaignName = strChildCampaignName;
            campaignGrade = strCampaignGrade;
            campaignMeetingLocation = strCampaignMeetingLocation;
            if(strcampaignMeetingDayfrequency ==null)
            strcampaignMeetingDayfrequency ='';
            campaignMeetingDay =strcampaignMeetingDayfrequency +' '+ strcampaignMeetingDay;
            campaignMeetingDayfrequency =  strcampaignMeetingDayfrequency ;
            if(TroopStartDate!= null && MeetingStartTime!=null ){
                  //String Str0 = String.valueOf(TroopStartDate);// +' '+ MeetingStartTime;
                 // system.debug('Str0======>'+Str0 +'TroopStartDate:'+TroopStartDate+'strCampaignMeetingStartDatetime:'+strCampaignMeetingStartDatetime);
                 // DateTime st =Datetime.parse(Str0);
                 //campaignMeetingStartDatetimeStd = String.valueOf(st.getTime());
                 Datetime   strCampaignMeetingStartDatetime= TroopStartDate;
                    String Str0 = MeetingStartTime;//convert in 24 hours format
                    String[] strarr = Str0.split(' ');
                    Integer hour = Integer.valueof(strarr.get(0).split(':').get(0));
                    Integer min = Integer.valueof(strarr.get(0).split(':').get(1));
                    string AMPM     = strarr.get(1);
                    if(hour!=12 && AMPM!='AM' )
                        hour=hour+12;
                         hour=hour+7;
                    Integer year=strCampaignMeetingStartDatetime.year();
                    Integer mon=strCampaignMeetingStartDatetime.month();
                    Integer day=strCampaignMeetingStartDatetime.day();
                    Datetime myDate = datetime.newInstance(year, mon, day , hour, min, 0);
                        
                     campaignMeetingStartDatetimeStd = String.valueOf(myDate.getTime());
             
            }else{
                 //campaignMeetingStartDatetime = strCampaignMeetingStartDatetime;
                 
                          campaignMeetingStartDatetimeStd = '';
                   
            
            }
            
           // campaignMeetingStartDatetime = strCampaignMeetingStartDatetime;
          
            campaignVolunteersRequired = strCampaignVolunteersRequired;
            parentCampaignName = strParentCampaignName;
            parentCampaignAccountId = strAccountId;
            childCampaignId = strChildCampaignId;
               campaignId = strChildCampaignId;
              campaignParticipationType = strCampaignParticipation;
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