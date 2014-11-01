public with sharing class GirlCatalog_TroopSearchController extends SobjectExtension {

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
    public static final Map<String, Schema.FieldSet> FIELDSETS_CAMPAIGN = SObjectType.Campaign.FieldSets.getMap();

    private String contactId;
    private String girlContactId;
    private String parentContactId;
    private String councilId { get; set; }

    public GirlCatalog_TroopSearchController() {
        selectedPageSize = '10';
        showSearchResultTable = false;
        whyAreYouUnsure = '';
        pagerFlag = false;

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

        system.debug('=== girlContactId ==: ' + girlContactId + ' :=parentContactId=: ' + parentContactId);

        Contact girlContact = getContactRecord();
        system.debug('=== girlContact ===:  ' + girlContact);

        if(girlContact != null && girlContact.Id != null) {
            zipCode = girlContact.MailingPostalCode;
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
                 return addErrorMessageAndRollback(savepoint,'No Troop exist with given zip code');
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
                    return addErrorMessageAndRollback(savepoint,'No Troop exist with given zip code');
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

            List<Campaign> allCampaignList = GirlRegistrationUtilty.getListOfCampaign(troopOrGroupName, 'TroopName', new Set<String>(), Grade);
            //List<Campaign> allCampaignList = GirlRegistrationUtilty.getListOfCampaign(troopOrGroupName, 'TroopName');
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
            system.debug('-------zipCode===>'+zipCode);
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