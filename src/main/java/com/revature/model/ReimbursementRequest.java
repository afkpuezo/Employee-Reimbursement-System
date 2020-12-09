/**
 * This class represents Reimbursement Requests within the application.
 * It does not do any verification of the data it is given; that should be
 * done before creating an object of this class. 
 * 
 * @author Andrew Curry
 */
package com.revature.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="REIMBURSEMENT_REQUEST")
public class ReimbursementRequest implements Serializable{

    // constants
    public static final int NULL_ID = -1; // can't make ints null???
    public static final String TIME_DEFAULT = "--";
    private static final long serialVersionUID = 0L; // makes compiler happy
    
    
    // enums ---------------------

    /**
     * Describes what kind of expense
     */
    public enum ReimbursementType{

        NONE(0, "NONE"), // never used?
        LODGING(1, "LODGING"),
        TRAVEL(2, "TRAVEL"),
        FOOD(3, "FOOD"),
        OTHER(4, "OTHER");

        private int ID;
        private String name;

        private ReimbursementType(int ID, String name){
            this.ID = ID;
            this.name = name;
        }

        public int getID() {
            return ID;
        }

        public String getName() {
            return name;
        }

        /**
         * Turns out the advanced syntax didn't help with this
         */
        public static ReimbursementType fromString(String s){

            switch(s){
                case "LODGING":
                    return LODGING;
                case "TRAVEL":
                    return TRAVEL;
                case "FOOD":
                    return FOOD;
                case "OTHER":
                    return OTHER;
                default:
                    return NONE;
            }
        }
    } // end enum TYpe

    /**
     * Describes whether the request has been approved or not
     */
    public enum ReimbursementStatus{

        NONE(0, "NONE"), // never used?
        PENDING(1, "PENDING"),
        APPROVED(2, "APPROVED"),
        DENIED(3, "DENIED");

        private int ID;
        private String name;

        private ReimbursementStatus(int ID, String name){
            this.ID = ID;
            this.name = name;
        }

        public int getID() {
            return ID;
        }

        public String getName() {
            return name;
        }
    } // end enum Status
    
    // class/static variables ---------------------

    // instance variables ---------------------
    @Id
    @Column(name="RR_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int ID;

    //private int authorID; // user who submitted this req
    @ManyToOne
	@JoinColumn(name="USER_ID", nullable=false, referencedColumnName = "USER_ID")
    private UserProfile author;

    @Column(name="RR_MONEY_AMOUNT")
    private long moneyAmount;

    @Column(name="RR_TYPE")
    @Enumerated(EnumType.STRING)
    private ReimbursementType type; // what kind of expense

    @Column(name="RR_STATUS")
    @Enumerated(EnumType.STRING)
    private ReimbursementStatus status; // has it been approved or not

    @Column(name="RR_DESCRIPTION")
    private String description;

    @Column(name="RR_TIME_SUBMITTED")
    private String timeSubmitted;

    @Column(name="RR_RESOLVER_ID")
    private int resolverID; // manager

    @Column(name="RR_TIME_RESOLVED")
    private String timeResolved;

    // optionally, some way of representing an image
    
    // constructor(s) ---------------------

    /**
     * (I think) This should only be used by Hibernate
     */
    public ReimbursementRequest(){

    }

    /**
     * Used when submitting/writing a new reimb-req.
     * Defaults to ID = -1 and PENDING status
     * @param ID
     * @param author
     * @param moneyAmount
     * @param type
     */
    public ReimbursementRequest(UserProfile author, long moneyAmount, ReimbursementType type){

        this.ID = NULL_ID;
        this.author = author;
        this.moneyAmount = moneyAmount;
        this.type = type;
        this.status = ReimbursementStatus.PENDING;
        this.description = "";
        this.timeSubmitted = TIME_DEFAULT;
        this.resolverID = NULL_ID;
        this.timeResolved = TIME_DEFAULT;
    }

    /**
     * Defaults to PENDING status
     * @param ID
     * @param author
     * @param moneyAmount
     * @param type
     */
    public ReimbursementRequest(
            int ID, UserProfile author, long moneyAmount, ReimbursementType type){

        this.ID = ID;
        this.author = author;
        this.moneyAmount = moneyAmount;
        this.type = type;
        this.status = ReimbursementStatus.PENDING;
        this.description = "";
        this.timeSubmitted = TIME_DEFAULT;
        this.resolverID = NULL_ID;
        this.timeResolved = TIME_DEFAULT;
    }

    /**
     * 
     * @param ID
     * @param author
     * @param moneyAmount
     * @param type
     * @param status
     * @param description
     * @param timeSubmitted,
     * @param resolvedID
     * @param timeResolved
     */
    public ReimbursementRequest(
            int ID, 
            UserProfile author, 
            long moneyAmount, 
            ReimbursementType type,
            ReimbursementStatus status,
            String description,
            String timeSubmitted,
            int resolverID,
            String timeResolved) {

        this.ID = ID;
        this.author = author;
        this.moneyAmount = moneyAmount;
        this.type = type;
        this.status = status;
        this.description = description;
        this.timeSubmitted = timeSubmitted;
        this.resolverID = resolverID;
        this.timeResolved = timeResolved;
    }

    // getters and setters ---------------------
    // currently no way to set ID, authorID, money, or type

    public int getID() {
        return this.ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getAuthorID() {
        return author.getID();
    }

    public UserProfile getAuthor(){
        return author;
    }

    public void setAuthor(UserProfile author){
        this.author = author;
    }

    public long getMoneyAmount() {
        return this.moneyAmount;
    }

    public void setMoneyAmount(long moneyAmount) {
        this.moneyAmount = moneyAmount;
    }

    public ReimbursementType getType() {
        return this.type;
    }

    public void setType(ReimbursementType type) {
        this.type = type;
    }

    public ReimbursementStatus getStatus() {
        return this.status;
    }

    public void setStatus(ReimbursementStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTimeSubmitted() {
        return this.timeSubmitted;
    }

    public void setTimeSubmitted(String timeSubmitted) {
        this.timeSubmitted = timeSubmitted;
    }

    public int getResolverID() {
        return this.resolverID;
    }

    public void setResolverID(int resolverID) {
        this.resolverID = resolverID;
    }

    public String getTimeResolved() {
        return this.timeResolved;
    }

    public void setTimeResolved(String timeResolved) {
        this.timeResolved = timeResolved;
    }

}
