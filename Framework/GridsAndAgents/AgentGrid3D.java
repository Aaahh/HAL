package Framework.GridsAndAgents;

import Framework.Interfaces.*;
import Framework.Rand;
import Framework.Util;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Extend the Grid2unstackable class if you want a 3D lattice with one or more agents per typeGrid voxel
 * @param <T> the AgentSQ3D or AgentPT3D extending agent class that will inhabit the typeGrid
 */
public class AgentGrid3D<T extends AgentBaseSpatial> extends GridBase3D implements Iterable<T>{
    InternalGridAgentList<T> agents;
    T[] grid;
    ArrayList<ArrayList<T>> usedAgentSearches =new ArrayList<>();
    ArrayList<AgentsIterator3D> usedIterIs =new ArrayList<>();
    int iagentSearch=0;
    int[] counts;


    /**
     * @param agentClass pass T.class, used to instantiate agent instances within the typeGrid as needed
     */
    public AgentGrid3D(int x, int y, int z, Class<T> agentClass, boolean wrapX, boolean wrapY, boolean wrapZ){
        super(x,y,z,wrapX,wrapY,wrapZ);
        agents=new InternalGridAgentList<T>(agentClass,this);
        grid=(T[])new AgentBaseSpatial[length];
        counts= new int[length];
    }
    public AgentGrid3D(int x, int y, int z, Class<T> agentClass){
        this(x,y,z,agentClass,false,false,false);
    }
//    void AddAgentToSquare(T agent,int iGrid){
//        //internal function, adds agent to typeGrid voxel
//        if(typeGrid[iGrid]==null) {
//            typeGrid[iGrid]=agent;
//        }
//        else{
//            typeGrid[iGrid].prevSq=agent;
//            agent.nextSq=typeGrid[iGrid];
//            typeGrid[iGrid]=agent;
//        }
//        counts[iGrid]++;
//    }
//    void RemAgentFromSquare(T agent,int iGrid){
//        //internal function, removes agent from typeGrid voxel
//        if(typeGrid[iGrid]==agent){
//            typeGrid[iGrid]=(T)agent.nextSq;
//        }
//        if(agent.nextSq!=null){
//            agent.nextSq.prevSq=agent.prevSq;
//        }
//        if(agent.prevSq!=null){
//            agent.prevSq.nextSq=agent.nextSq;
//        }
//        agent.prevSq=null;
//        agent.nextSq=null;
//        counts[iGrid]--;
//    }
//public void IncTick(){
//    agents.stateID++;
//}
    T GetNewAgent(){
        return agents.GetNewAgent();
    }

    /**
     * returns an uninitialized agent at the specified coordinates
     */
    public T NewAgentSQ(int x, int y, int z){
        T newAgent=GetNewAgent();
        newAgent.Setup(x,y,z);
        return newAgent;
    }

    /**
     * returns an uninitialized agent at the specified coordinates
     */
    public T NewAgentPT(double x, double y, double z){
        T newAgent=GetNewAgent();
        newAgent.Setup(x,y,z);
        return newAgent;
    }
    /**
     * returns an uninitialized agent at the specified index
     */
    public T NewAgentSQ(int index){
        T newAgent=agents.GetNewAgent();
        newAgent.Setup(index);
        return newAgent;
    }
//    void RemoveNode(T agent,int iGrid){
//        //internal function, removes agent from world
//        RemAgentFromSquare(agent, iGrid);
//        agents.RemoveNode(agent);
//    }


    public void CleanShuffle(Rand rn){
        CleanAgents();
        ShuffleAgents(rn);
    }
    /**
     * shuffles the agent list to randomize iteration
     * do not call this while in the middle of iteration
     * @param rn the Random number generator to be used
     */
    public void ShuffleAgents(Rand rn){
        agents.ShuffleAgents(rn);
    }

    /**
     * cleans the list of agents, removing dead ones, may improve the efficiency of the agent iteration if many agents have died
     * do not call this while in the middle of iteration
     */
    public void CleanAgents(){
        agents.CleanAgents();
    }

//    public void CleanShuffInc(Rand rn){
//        CleanAgents();
//        ShuffleAgents(rn);
//        IncTick();
//    }
//    public void ShuffInc(Rand rn){
//        ShuffleAgents(rn);
//        IncTick();
//    }
//    public void CleanInc(){
//        CleanAgents();
//        IncTick();
//    }
    public int FindEmptyIs(int[] IsToCheck,int lenToCheck){
        int validCount=0;
        for (int i = 0; i < lenToCheck; i++) {
            if(GetAgent(IsToCheck[i])==null){
                IsToCheck[validCount]=IsToCheck[i];
                validCount++;
            }
        }
        return validCount;
    }
    public int FindEmptyIs(int[] IsToCheck,int[]retIs,int lenToCheck){
        int validCount=0;
        for (int i = 0; i < lenToCheck; i++) {
            if(GetAgent(IsToCheck[i])==null){
                retIs[validCount]=IsToCheck[i];
                validCount++;
            }
        }
        return validCount;
    }

//    public void MultiThreadAgents(int nThreads, AgentStepFunction<T> StepFunction){
//        int last=agents.iLastAlive;
//        Util.MultiThread(nThreads,nThreads,(iThread)->{
//            ArrayList<T> agents=this.agents.agents;
//            int start=iThread/nThreads*last;
//            int end=(iThread+1)/nThreads*last;
//            for (int i = start; i < end; i++) {
//                T a=agents.get(i);
//                if(a.alive&&a.stateID<st){
//                    StepFunction.AgentStepFunction(a);
//                }
//            }
//        });
//    }
    public int FindOccupiedIs(int[] IsToCheck,int lenToCheck){
        int validCount=0;
        for (int i = 0; i < lenToCheck; i++) {
            if(GetAgent(IsToCheck[i])!=null){
                IsToCheck[validCount]=IsToCheck[i];
                validCount++;
            }
        }
        return validCount;
    }
    public int FindOccupiedIs(int[] IsToCheck,int[] retIs,int lenToCheck){
        int validCount=0;
        for (int i = 0; i < lenToCheck; i++) {
            if(GetAgent(IsToCheck[i])!=null){
                retIs[validCount]=IsToCheck[i];
                validCount++;
            }
        }
        return validCount;
    }
    public int HoodToEmptyIs(int[] hood, int[] ret, int centerX, int centerY, int centerZ, boolean wrapX, boolean wrapY, boolean wrapZ){
        int toCheck= HoodToIs(hood,ret,centerX,centerY,centerZ,wrapX,wrapY,wrapZ);
        return FindEmptyIs(ret,toCheck);
    }
    public int HoodToEmptyIs(int[] hood, int[] ret, int centerX, int centerY, int centerZ){
        int toCheck= HoodToIs(hood,ret,centerX,centerY,centerZ);
        return FindEmptyIs(ret,toCheck);
    }
    public int HoodToOccupiedIs(int[] hood, int[] ret, int centerX, int centerY, int centerZ){
        int toCheck= HoodToIs(hood,ret,centerX,centerY,centerZ);
        return FindOccupiedIs(ret,toCheck);
    }
    public int HoodToOccupiedIs(int[] hood, int[] ret, int centerX, int centerY, int centerZ, boolean wrapX, boolean wrapY, boolean wrapZ){
        int toCheck= HoodToIs(hood,ret,centerX,centerY,centerZ,wrapX,wrapY,wrapZ);
        return FindOccupiedIs(ret,toCheck);
    }
    public void ChangeGridsSQ(T foreignAgent,int newX,int newY,int newZ){
        if(!foreignAgent.alive){
            throw new IllegalStateException("can't move dead agent between grids!");
        }
        if(foreignAgent.myGrid.getClass()!=this.getClass()){
            throw new IllegalStateException("can't move agent to a different type of typeGrid!");
        }
        foreignAgent.RemSQ();
        ((AgentGrid3D)foreignAgent.myGrid).agents.RemoveAgent(foreignAgent);
        agents.AddAgent(foreignAgent);
        foreignAgent.Setup(newX,newY,newZ);
    }
    public void ChangeGridsPT(T foreignAgent,double newX,double newY,double newZ){
        if(!foreignAgent.alive){
            throw new IllegalStateException("can't move dead agent between grids!");
        }
        if(foreignAgent.myGrid.getClass()!=this.getClass()){
            throw new IllegalStateException("can't move agent to a different type of typeGrid!");
        }
        foreignAgent.RemSQ();
        ((AgentGrid3D)foreignAgent.myGrid).agents.RemoveAgent(foreignAgent);
        agents.AddAgent(foreignAgent);
        foreignAgent.Setup(newX,newY,newZ);
    }
    public void ChangeGridsSQ(T foreignAgent,int newI){
        if(!foreignAgent.alive){
            throw new IllegalStateException("can't move dead agent between grids!");
        }
        if(foreignAgent.myGrid.getClass()!=this.getClass()){
            throw new IllegalStateException("can't move agent to a different type of typeGrid!");
        }
        foreignAgent.RemSQ();
        ((AgentGrid3D)foreignAgent.myGrid).agents.RemoveAgent(foreignAgent);
        agents.AddAgent(foreignAgent);
        foreignAgent.Setup(newI);
    }

    public int MapEmptyHood(int[] hood,int centerX,int centerY,int centerZ){
        int toCheck=MapHood(hood,centerX,centerY,centerZ);
        return FindEmptyIs(hood,toCheck);
    }
    public int MapEmptyHood(int[] hood,int centerI){
        int toCheck=MapHood(hood,centerI);
        return FindEmptyIs(hood,toCheck);
    }
    public int MapOccupiedHood(int[] hood,int centerX,int centerY,int centerZ){
        int toCheck=MapHood(hood,centerX,centerY,centerZ);
        return FindOccupiedIs(hood,toCheck);
    }
    public int MapOccupiedHood(int[] hood,int centerI){
        int toCheck=MapHood(hood,centerI);
        return FindOccupiedIs(hood,toCheck);
    }

    /**
     * returns the number of agents on the voxel at the specified coordinates
     */
//    public int PopAt(int x, int y, int z){
//        //gets population count at location
//        return counts[I(x,y,z)];
//    }
    /**
     * returns the number of agents on the voxel at the specified index
     */
//    public int PopAt(int i){
//        //gets population count at location
//        return counts[i];
//    }
    public T NewAgentPTSafe(double newX, double newY, double newZ, double fallbackX, double fallbackY, double fallbackZ, boolean wrapX, boolean wrapY, boolean wrapZ){
        if (In(newX, newY,newZ)) {
            return NewAgentPT(newX, newY,newZ);
        }
        if (wrapX) {
            newX = Util.ModWrap(newX, xDim);
        } else if (!Util.InDim(xDim, newX)) {
            newX = fallbackX;
        }
        if (wrapY) {
            newY = Util.ModWrap(newY, yDim);
        } else if (!Util.InDim(yDim, newY))
            newY = fallbackY;
        if (wrapZ) {
            newZ = Util.ModWrap(newZ, zDim);
        } else if (!Util.InDim(zDim, newZ))
            newZ = fallbackZ;
        if(!In(newX,newY,newZ)){
            System.out.println("");
        }
        return NewAgentPT(newX,newY,newZ);
    }
    public T NewAgentPTSafe(double newX, double newY, double newZ, double fallbackX, double fallbackY, double fallbackZ){
        if (In(newX, newY,newZ)) {
            return NewAgentPT(newX, newY,newZ);
        }
        if (wrapX) {
            newX = Util.ModWrap(newX, xDim);
        } else if (!Util.InDim(xDim, newX)) {
            newX = fallbackX;
        }
        if (wrapY) {
            newY = Util.ModWrap(newY, yDim);
        } else if (!Util.InDim(yDim, newY))
            newY = fallbackY;
        if (wrapZ) {
            newZ = Util.ModWrap(newZ, zDim);
        } else if (!Util.InDim(zDim, newZ))
            newZ = fallbackZ;
        return NewAgentPT(newX,newY,newZ);
    }
    /**
     * returns an umodifiable copy of the complete agentlist, including dead and just born agents
     */
    public ArrayList<T> AllAgents(){
        return new ArrayList<>(this.agents.GetAllAgents());
    }

    /**
     * Gets the last agent to move to the specified coordinates
     * not recommended if the model calls for multiple agents on the same square, as it will only return one of these
     * returns null if no agent exists
     */
    public T GetAgent(int x, int y, int z){
        return grid[I(x,y,z)];
    }
    public T GetAgentSafe(int x, int y,int z) {
        if (wrapX) {
            x = Util.ModWrap(x, xDim);
        } else if (!Util.InDim(xDim, x)) {
            return null;
        }
        if (wrapY) {
            y = Util.ModWrap(y, yDim);
        } else if (!Util.InDim(yDim, y)) {
            return null;
        }
        if (wrapZ) {
            z = Util.ModWrap(z, yDim);
        } else if (!Util.InDim(yDim, z)) {
            return null;
        }
        return grid[I(x,y,z)];
    }
    /**
     * Gets the last agent to move to the specified index
     * not recommended if the model calls for multiple agents on the same square, as it will only return one of these
     * returns null if no agent exists
     */
    public T GetAgent(int index){
        return grid[index];
    }
    public void GetAgentsRect(ArrayList<T> retAgentList, int x, int y,int z, int width, int height,int depth){
        int xEnd=x+width;
        int yEnd=y+height;
        int zEnd=z+depth;
        int xWrap;
        int yWrap;
        int zWrap;
        for (int xi = x; xi <= xEnd; xi++) {
            boolean inX= Util.InDim(xDim,xi);
            if((!wrapX&&!inX)){
                continue;
            }
            xWrap=xi;
            if(wrapX&&!inX){
                xWrap= Util.ModWrap(xi,xDim);
            }
            for (int yi = y; yi <= yEnd; yi++) {
                boolean inY= Util.InDim(yDim,yi);
                if((!wrapY&&!inY)){
                    continue;
                }
                yWrap=yi;
                if(wrapY&&!inY){
                    yWrap= Util.ModWrap(yi,yDim);
                }
                for (int zi = z; zi <= zEnd; zi++) {
                    boolean inZ = Util.InDim(zDim, zi);
                    if ((!wrapZ && !inZ)) {
                        continue;
                    }
                    zWrap = zi;
                    if (wrapZ && !inZ) {
                        zWrap = Util.ModWrap(zi, zDim);
                    }
                    GetAgents(retAgentList, xWrap, yWrap,zWrap);
                }
            }
        }
    }
    public void GetAgentsHood(ArrayList<T> retAgentList,int[] hood,int centerX,int centerY,int centerZ){
        int iStart=hood.length/4;
        for (int i = iStart; i < hood.length; i+=3) {
            int x = hood[i] + centerX;
            int y = hood[i + 1] + centerY;
            int z = hood[i + 2] + centerZ;
            if (!Util.InDim(xDim, x)) {
                if (wrapX) {
                    x = Util.ModWrap(x, xDim);
                } else {
                    continue;
                }
            }
            if (!Util.InDim(yDim, y)) {
                if (wrapY) {
                    y = Util.ModWrap(y, yDim);
                } else {
                    continue;
                }
            }
            if (!Util.InDim(zDim, z)) {
                if (wrapZ) {
                    z = Util.ModWrap(z, zDim);
                } else {
                    continue;
                }
            }
            GetAgents(retAgentList, x, y, z);
        }

    }

    public void GetAgentsHoodMapped(ArrayList<T> retAgentList,int[] hood,int hoodLen){
        for(int i=0;i<hoodLen;i++) {
            GetAgents(retAgentList,hood[i]);
        }
    }
    public void GetAgentsRadApprox(final ArrayList<T> retAgentList, final double x, final double y, final double z, final double rad, boolean wrapX, boolean wrapY, boolean wrapZ){
        int nAgents;
        for (int xSq = (int)Math.floor(x-rad); xSq <(int)Math.ceil(x+rad) ; xSq++) {
            for (int ySq = (int)Math.floor(y-rad); ySq <(int)Math.ceil(y+rad) ; ySq++) {
                for (int zSq = (int)Math.floor(z-rad); zSq <(int)Math.ceil(z+rad) ; zSq++) {
                    int retX = xSq;
                    int retY = ySq;
                    int retZ = zSq;
                    boolean inX = Util.InDim(xDim, retX);
                    boolean inY = Util.InDim(yDim, retY);
                    boolean inZ = Util.InDim(zDim, retZ);
                    if ((!wrapX && !inX) || (!wrapY && !inY) || (!wrapZ && !inZ)) {
                        continue;
                    }
                    if (wrapX && !inX) {
                        retX = Util.ModWrap(retX, xDim);
                    }
                    if (wrapY && !inY) {
                        retY = Util.ModWrap(retY, yDim);
                    }
                    if (wrapZ && !inZ) {
                        retZ = Util.ModWrap(retZ, zDim);
                    }
                    GetAgents(retAgentList, I(retX, retY, retZ));
                }
            }
        }
    }
    public void GetAgentsRad(final ArrayList<T> retAgentList, final double x, final double y, final double z, final double rad, boolean wrapX, boolean wrapY, boolean wrapZ){
        int nAgents;
        double radSq=rad*rad;
        for (int xSq = (int)Math.floor(x-rad); xSq <(int)Math.ceil(x+rad) ; xSq++) {
            for (int ySq = (int)Math.floor(y-rad); ySq <(int)Math.ceil(y+rad) ; ySq++) {
                for (int zSq = (int)Math.floor(z-rad); zSq <(int)Math.ceil(z+rad) ; zSq++) {
                    int retX = xSq;
                    int retY = ySq;
                    int retZ = zSq;
                    boolean inX = Util.InDim(xDim, retX);
                    boolean inY = Util.InDim(yDim, retY);
                    boolean inZ = Util.InDim(zDim, retZ);
                    if ((!wrapX && !inX) || (!wrapY && !inY) || (!wrapZ && !inZ)) {
                        continue;
                    }
                    if (wrapX && !inX) {
                        retX = Util.ModWrap(retX, xDim);
                    }
                    if (wrapY && !inY) {
                        retY = Util.ModWrap(retY, yDim);
                    }
                    if (wrapZ && !inZ) {
                        retZ = Util.ModWrap(retZ, zDim);
                    }
                    GetAgents(retAgentList,(agent)->{
                        Agent3DBase a=(Agent3DBase)agent;
                    return Util.DistSquared(a.Xpt(),a.Ypt(),a.Zpt(),x,y,z,xDim,yDim,zDim,wrapX,wrapY,wrapZ)<radSq;
                },xSq,ySq,zSq);
                }
            }
        }
    }
    public void GetAgentsRadApprox(final ArrayList<T> retAgentList, final double x, final double y, final double z, final double rad){
        GetAgentsRadApprox(retAgentList,x,y,z,rad,wrapX,wrapY,wrapZ);
    }
    /**
     * calls dispose on all agents in the typeGrid, resets the tick timer to 0.
     */
    public void Reset(){
//        IncTick();
        for (T a : this) {
            a.Dispose();
        }
        if(GetPop()>0){
            throw new IllegalStateException("Something is wrong with Reset, tell Rafael Bravo to fix this!");
        }
//        tick=0;
    }
    public void ResetHard(){
//        IncTick();
        for (T a : this) {
            a.Dispose();
        }
        if(GetPop()>0){
            throw new IllegalStateException("Something is wrong with Reset, tell Rafael Bravo to fix this!");
        }
        this.agents.Reset();
//        tick=0;
    }

    public int CountAt(int i){
        T agent=grid[i];
        if(agent==null){
            return 0;
        }
        return agent.GetCountOnSquare();
    }
    public int CountAt(int x,int y,int z){
        return CountAt(I(x,y,z));
    }
    public int CountAt(int i,AgentToBool EvalAgent){
        T agent=grid[i];
        if(agent==null){
            return 0;
        }
        return agent.GetCountOnSquareEval(EvalAgent);
    }
    public int CountAt(int x,int y,int z,AgentToBool EvalAgent){
        return CountAt(I(x,y,z),EvalAgent);
    }

    ArrayList<T> GetFreshAgentSearchArr(){
        ArrayList<T> agents;
        if(iagentSearch>= usedAgentSearches.size()){
            agents=new ArrayList<T>();
            usedAgentSearches.add(agents);
        }
        else{
            agents= usedAgentSearches.get(iagentSearch);
            agents.clear();
        }
        return agents;
    }

    public int ApplyAgentsRad(double rad, double x, double y,double z, AgentRadDispToAction3D<T> action){
        ArrayList<T> agents = GetFreshAgentSearchArr();
        iagentSearch++;
        int nAgents=0;
        double radSq=rad*rad;
        for (int xSq = (int)Math.floor(x-rad); xSq <(int)Math.ceil(x+rad) ; xSq++) {
            for (int ySq = (int)Math.floor(y-rad); ySq <(int)Math.ceil(y+rad) ; ySq++) {
                for (int zSq = (int)Math.floor(z-rad); zSq <(int)Math.ceil(z+rad) ; zSq++) {
                    int retX = xSq;
                    int retY = ySq;
                    int retZ = zSq;
                    boolean inX = Util.InDim(xDim, retX);
                    boolean inY = Util.InDim(yDim, retY);
                    boolean inZ = Util.InDim(zDim, retZ);
                    if ((!wrapX && !inX) || (!wrapY && !inY) || (!wrapZ && ! inZ)) {
                        continue;
                    }
                    if (wrapX && !inX) {
                        retX = Util.ModWrap(retX, xDim);
                    }
                    if (wrapY && !inY) {
                        retY = Util.ModWrap(retY, yDim);
                    }
                    if (wrapZ && !inZ) {
                        retZ = Util.ModWrap(retZ, zDim);
                    }
                    GetAgents(agents, retX, retY, retZ);
                    for (int i = 0; i < agents.size(); i++) {
                        T agent = agents.get(i);
                        double xDisp = ((Agent3DBase) (agent)).Xpt() - x;
                        double yDisp = ((Agent3DBase) (agent)).Ypt() - y;
                        double zDisp = ((Agent3DBase) (agent)).Zpt() - z;
                        double distSq = xDisp * xDisp + yDisp * yDisp + zDisp*zDisp;
                        if (distSq <= radSq) {
                            action.Action(agent, xDisp, yDisp,zDisp, distSq);
                            nAgents++;
                        }
                    }
                    agents.clear();
                }
            }
        }
        iagentSearch--;
        return nAgents;
    }

    public int ApplyAgentsSQ(int i, AgentToAction<T> action) {
        return ApplyAgentsSQ(-1, i, null,null,action);
    }
    public int ApplyAgentsSQ(int x, int y,int z, AgentToAction<T> action) {
        return ApplyAgentsSQ(-1, I(x,y,z), null,null,action);
    }
    public int ApplyAgentsSQ(int i, AgentToBool<T> evalAgent, AgentToAction<T> action) {
        return ApplyAgentsSQ(-1, i, null,evalAgent,action);
    }
    public int ApplyAgentsSQ(int x, int y,int z, AgentToBool<T> evalAgent, AgentToAction<T> action) {
        return ApplyAgentsSQ(-1, I(x,y,z), null,evalAgent,action);
    }
    public int ApplyAgentsSQ(int nActions, int x, int y,int z, Rand rn, AgentToAction<T> action) {
        return ApplyAgentsSQ(nActions, I(x,y,z), rn,null,action);
    }
    public int ApplyAgentsSQ(int nActions, int i, Rand rn, AgentToAction<T> action) {
        return ApplyAgentsSQ(nActions, i, rn,null,action);

    }
    public int ApplyAgentsSQ(int nActions, int x, int y,int z, Rand rn, AgentToBool<T> evalAgent, AgentToAction<T> action) {
        return ApplyAgentsSQ(nActions, I(x,y,z), rn,evalAgent,action);
    }
    public int ApplyAgentsSQ(int nActions, int i, Rand rn, AgentToBool<T> evalAgent, AgentToAction<T> action) {
        ArrayList<T> agents = GetFreshAgentSearchArr();
        iagentSearch++;
        if(evalAgent!=null) {
            GetAgents(agents, evalAgent, i);
        }
        else{
            GetAgents(agents, i);
        }
        int nFound = agents.size();
        if (nActions < 0 || nFound <= nActions) {
            for (T agent : agents) {
                action.Action(agent,nFound);
            }
        } else {
            for (int j = 0; j < nActions; j++) {
                int iRand = rn.Int(nFound - j);
                T agent=agents.get(iRand);
                action.Action(agent, nFound);
                agents.set(iRand,agents.get(nFound - j - 1));
            }
        }
        iagentSearch--;
        return nFound;
    }
    public int ApplyAgentsHood(int[]hood, int centerI, AgentToAction<T> action) {
        return ApplyAgentsHood(-1, hood,ItoX(centerI),ItoY(centerI),ItoZ(centerI), null, null, null,action,wrapX,wrapY,wrapZ);
    }
    public int ApplyAgentsHood(int[]hood, int centerX, int centerY, int centerZ, AgentToAction<T> action) {
        return ApplyAgentsHood(-1, hood,centerX,centerY,centerZ, null, null, null,action,wrapX,wrapY,wrapZ);
    }
    public int ApplyAgentsHood(int[]hood, int centerI, AgentToBool<T> evalAgent, AgentToAction<T> action) {
        return ApplyAgentsHood(-1, hood,ItoX(centerI),ItoY(centerI),ItoZ(centerI), null, null, evalAgent,action,wrapX,wrapY,wrapZ);
    }
    public int ApplyAgentsHood(int[]hood, int centerX, int centerY, int centerZ, AgentToBool<T> evalAgent, AgentToAction<T> action) {
        return ApplyAgentsHood(-1, hood,centerX,centerY,centerZ, null, null, evalAgent,action,wrapX,wrapY,wrapZ);
    }
    public int ApplyAgentsHood(int[]hood, int centerI, int nActions, Rand rn, AgentToAction<T> action) {
        return ApplyAgentsHood(nActions, hood,ItoX(centerI),ItoY(centerI),ItoZ(centerI), rn, null, null,action,wrapX,wrapY,wrapZ);
    }
    public int ApplyAgentsHood(int nActions, int[] hood, int centerX, int centerY, int centerZ, Rand rn, AgentToAction<T> action) {
        return ApplyAgentsHood(nActions, hood,centerX,centerY,centerZ, rn, null, null,action,wrapX,wrapY,wrapZ);
    }
    public int ApplyAgentsHood(int nActions, int[] hood, int centerI, Rand rn, AgentToBool<T> evalAgent, AgentToAction<T> action) {
        return ApplyAgentsHood(nActions, hood,ItoX(centerI),ItoY(centerI),ItoZ(centerI), rn, null, evalAgent,action,wrapX,wrapY,wrapZ);
    }
    public int ApplyAgentsHood(int nActions, int[] hood, int centerX, int centerY, int centerZ, Rand rn, AgentToBool<T> evalAgent, AgentToAction<T> action) {
        return ApplyAgentsHood(nActions, hood,centerX,centerY,centerZ, rn, null, evalAgent,action,wrapX,wrapY,wrapZ);
    }
    public int ApplyAgentsHood(int nActions, int[] hood, int centerI, Rand rn, AgentToBool<T> evalAgent, AgentToAction<T> action, boolean wrapX, boolean wrapY, boolean wrapZ) {
        return ApplyAgentsHood(nActions, hood,ItoX(centerI),ItoY(centerI),ItoZ(centerI), rn, null, evalAgent,action,wrapX,wrapY,wrapZ);
    }
    public int ApplyAgentsHood(int[] hood, int centerX, int centerY, int centerZ, Rand rn, IntToInt GetNumActions,AgentToAction<T> action) {
        return ApplyAgentsHood(-1, hood,centerX,centerY,centerZ, rn, GetNumActions, null,action,wrapX,wrapY,wrapZ);
    }
    public int ApplyAgentsHood(int[] hood, int centerI, Rand rn, AgentToBool<T> evalAgent,IntToInt GetNumActions, AgentToAction<T> action) {
        return ApplyAgentsHood(-1, hood,ItoX(centerI),ItoY(centerI),ItoZ(centerI), rn, GetNumActions, evalAgent,action,wrapX,wrapY,wrapZ);
    }
    public int ApplyAgentsHood(int[] hood, int centerX, int centerY, int centerZ, Rand rn,IntToInt GetNumActions, AgentToBool<T> evalAgent, AgentToAction<T> action) {
        return ApplyAgentsHood(-1, hood,centerX,centerY,centerZ, rn, GetNumActions, evalAgent,action,wrapX,wrapY,wrapZ);
    }
    public int ApplyAgentsHood(int[] hood, int centerI, Rand rn,IntToInt GetNumActions, AgentToBool<T> evalAgent, AgentToAction<T> action, boolean wrapX, boolean wrapY, boolean wrapZ) {
        return ApplyAgentsHood(-1, hood,ItoX(centerI),ItoY(centerI),ItoZ(centerI), rn, GetNumActions, evalAgent,action,wrapX,wrapY,wrapZ);
    }
    int ApplyAgentsHood(int nActions, int[] hood, int centerX, int centerY, int centerZ, Rand rn, IntToInt GetNumActions, AgentToBool<T> evalAgent, AgentToAction<T> action, boolean wrapX, boolean wrapY, boolean wrapZ) {
        ArrayList<T> agents = GetFreshAgentSearchArr();
        iagentSearch++;
        for (int i = hood.length/4; i < hood.length; i+=3) {
            int x = hood[i] + centerX;
            int y = hood[i + 1] + centerY;
            int z = hood[i + 2] + centerZ;
            if (!Util.InDim(xDim, x)) {
                if (wrapX) {
                    x = Util.ModWrap(x, xDim);
                } else {
                    continue;
                }
            }
            if (!Util.InDim(yDim, y)) {
                if (wrapY) {
                    y = Util.ModWrap(y, yDim);
                } else {
                    continue;
                }
            }
            if (!Util.InDim(zDim, z)) {
                if (wrapZ) {
                    z = Util.ModWrap(z, zDim);
                } else {
                    continue;
                }
            }
            if(evalAgent!=null) {
                GetAgents(agents, evalAgent, x, y,z);
            }
            else{
                GetAgents(agents, x, y,z);
            }
        }
        int nFound = agents.size();
        if (nActions < 0 || nFound <= nActions) {
            for (T agent : agents) {
                action.Action(agent,nFound);
            }
        } else {
            for (int i = 0; i < nActions; i++) {
                int iRand = rn.Int(nFound - i);
                T agent=agents.get(iRand);
                action.Action(agent, nFound);
                agents.set(iRand,agents.get(nFound - i - 1));
            }
        }
        iagentSearch--;
        return nFound;
    }

    int SubsetAgents(ArrayList<T>agents, AgentToBool<T> EvalAgent){
        int len=agents.size();
        int ret=0;
        for (int i = 0; i < len; i++) {
            T agent=agents.get(i);
            if(EvalAgent.EvalAgent(agent)){
                agents.set(ret,agent);
                ret++;
            }
        }
        return ret;
    }
    public T RandomAgent(Rand rn){
        CleanAgents();
        if(GetPop()==0){
            return null;
        }
        return agents.agents.get(rn.Int(GetPop()));
    }
    /**
     * appends to the provided arraylist all agents on the square at the specified coordinates
     * @param retAgentList the arraylist ot be added to
     */
    public void GetAgents(ArrayList<T>retAgentList, int x, int y, int z){
        T agent= GetAgent(x,y,z);
        if(agent!=null){
            agent.GetAllOnSquare(retAgentList);
        }
    }
    public void GetAgents(ArrayList<T>retAgentList, AgentToBool<T>evalAgent, int x, int y, int z){
        T agent= GetAgent(x,y,z);
        if(agent!=null){
            agent.GetAllOnSquareEval(retAgentList,evalAgent);
        }
    }

    public void GetAgents(ArrayList<T>retAgentList, AgentToBool<T>evalAgent, int i){
        T agent= GetAgent(i);
        if(agent!=null){
            agent.GetAllOnSquareEval(retAgentList,evalAgent);
        }
    }


    public void GetAgentsSafe(ArrayList<T>retAgentList, int x, int y, int z){
        if (wrapX) {
            x = Util.ModWrap(x, xDim);
        } else if (!Util.InDim(xDim, x)) {
            return;
        }
        if (wrapY) {
            y = Util.ModWrap(y, yDim);
        } else if (!Util.InDim(yDim, y)) {
            return;
        }
        if (wrapZ) {
            z = Util.ModWrap(z, yDim);
        } else if (!Util.InDim(yDim, z)) {
            return;
        }
        T agent= GetAgent(x,y,z);
        if(agent!=null){
            agent.GetAllOnSquare(retAgentList);
        }
    }
    /**
     * appends to the provided arraylist all agents on the square at the specified index
     * @param retAgentList the arraylist ot be added to
     */
    public void GetAgents(ArrayList<T>retAgentList, int index){
        T agent= grid[index];
        if(agent!=null){
            agent.GetAllOnSquare(retAgentList);
        }
    }

    /**
     * returns the number of agents that are alive in the typeGrid
     */
    public int GetPop(){
        //gets population
        return agents.pop;
    }

    public double DistSquared(double x1, double y1,double z1, double x2, double y2,double z2, boolean wrapX, boolean wrapY,boolean wrapZ){
        return Util.DistSquared(x1,y1,z1,x2,y2,z2, xDim, yDim,zDim, wrapX,wrapY,wrapZ);
    }
    public double DistSquared(double x1, double y1,double z1, double x2, double y2,double z2){
        return Util.DistSquared(x1,y1,z1,x2,y2,z2, xDim, yDim,zDim, wrapX,wrapY,wrapZ);
    }

    @Override
    public Iterator<T> iterator() {
            return agents.iterator();
    }


    public Iterable<T> IterAgentsSafe(int x,int y,int z){
        ArrayList<T> agents=GetFreshAgentSearchArr();
        GetAgentsSafe(agents,x,y,z);
        return GetFreshAgentsIterator(agents);
    }
    public Iterable<T> IterAgents(int x,int y,int z){
        ArrayList<T> agents=GetFreshAgentSearchArr();
        GetAgents(agents,x,y,z);
        return GetFreshAgentsIterator(agents);
    }
    public Iterable<T> IterAgents(int i){
        ArrayList<T> agents=GetFreshAgentSearchArr();
        GetAgents(agents,i);
        return GetFreshAgentsIterator(agents);
    }
    public Iterable<T> IterAgentsRadApprox(double x, double y, double z, double rad){
        ArrayList<T> agents=GetFreshAgentSearchArr();
        GetAgentsRadApprox(agents,x,y,z,rad,wrapX,wrapY,wrapZ);
        return GetFreshAgentsIterator(agents);
    }
    public Iterable<T> IterAgentsRad(double x, double y, double z, double rad){
        ArrayList<T> agents=GetFreshAgentSearchArr();
        GetAgentsRad(agents,x,y,z,rad,wrapX,wrapY,wrapZ);
        return GetFreshAgentsIterator(agents);
    }
    public Iterable<T> IterAgentsRect(int x, int y, int z, int width, int height, int depth){
        ArrayList<T> agents=GetFreshAgentSearchArr();
        GetAgentsRect(agents,x,y,z,width,height,depth);
        return GetFreshAgentsIterator(agents);
    }

    public Iterable<T> IterAgentsHood(int[]hood, int centerX, int centerY, int centerZ){
        ArrayList<T> myAgents=GetFreshAgentSearchArr();
        GetAgentsHood(myAgents,hood,centerX,centerY,centerZ);
        return GetFreshAgentsIterator(myAgents);
    }
    public Iterable<T> IterAgentsHood(int[]hood, int centerI){
        return IterAgentsHood(hood,ItoX(centerI),ItoY(centerI),ItoZ(centerI));
    }
    public Iterable<T> IterAgentsHoodMapped(int[]hood,int hoodLen){
        ArrayList<T> myAgents=GetFreshAgentSearchArr();
        GetAgentsHoodMapped(myAgents,hood,hoodLen);
        return GetFreshAgentsIterator(myAgents);
    }

    AgentsIterator3D GetFreshAgentsIterator(ArrayList<T> agents){
        AgentsIterator3D ret;
        if(usedIterIs.size()>0){
            ret=usedIterIs.remove(usedIterIs.size()-1);
        }
        else{
            ret=new AgentsIterator3D(this);
        }
        ret.Setup(agents);
        return ret;
    }
    public T RandomAgentSafe(int x,int y,int z,Rand rn){
        ArrayList<T> agents=GetFreshAgentSearchArr();
        GetAgentsSafe(agents,x,y,z);
        T ret= agents.get(rn.Int(agents.size()));
        usedAgentSearches.add(agents);
        return ret;
    }
    public T RandomAgent(int x,int y,int z,Rand rn){
        ArrayList<T> agents=GetFreshAgentSearchArr();
        GetAgents(agents,x,y,z);
        T ret= agents.get(rn.Int(agents.size()));
        usedAgentSearches.add(agents);
        return ret;
    }
    public T RandomAgent(int i,Rand rn){
        return RandomAgent(ItoX(i),ItoY(i),ItoZ(i),rn);
    }
    public T RandomAgentRad(double x,double y,double z,double rad,Rand rn){
        ArrayList<T> agents=GetFreshAgentSearchArr();
        GetAgentsRad(agents,x,y,z,rad,wrapX,wrapY,wrapZ);
        T ret= agents.get(rn.Int(agents.size()));
        return ret;
    }
    public T RandomAgentRect(int x,int y,int z,int width,int height,int depth,Rand rn){
        ArrayList<T> agents=GetFreshAgentSearchArr();
        GetAgentsRect(agents,x,y,z,width,height,depth);
        T ret= agents.get(rn.Int(agents.size()));
        usedAgentSearches.add(agents);
        return ret;
    }

    public T RandomAgentHood(int[] hood,int x,int y,int z,Rand rn){
        ArrayList<T> agents=GetFreshAgentSearchArr();
        GetAgentsHood(agents,hood,x,y,z);
        T ret= agents.get(rn.Int(agents.size()));
        usedAgentSearches.add(agents);
        return ret;
    }
    public T RandomAgentSafe(int x,int y,int z,Rand rn,AgentToBool<T> EvalAgent){
        ArrayList<T> agents=GetFreshAgentSearchArr();
        GetAgentsSafe(agents,x,y,z);
        int ct=SubsetAgents(agents,EvalAgent);
        T ret= agents.get(rn.Int(ct));
        usedAgentSearches.add(agents);
        return ret;
    }
    public T RandomAgent(int x,int y,int z,Rand rn,AgentToBool<T> EvalAgent){
        ArrayList<T> agents=GetFreshAgentSearchArr();
        GetAgents(agents,x,y,z);
        int ct=SubsetAgents(agents,EvalAgent);
        T ret= agents.get(rn.Int(ct));
        usedAgentSearches.add(agents);
        return ret;
    }
    public T RandomAgent(int i,Rand rn,AgentToBool<T> EvalAgent){
        return RandomAgent(ItoX(i),ItoY(i),ItoZ(i),rn,EvalAgent);
    }
    public T RandomAgentRad(double x,double y,double z,double rad,Rand rn,AgentToBool<T> EvalAgent){
        ArrayList<T> agents=GetFreshAgentSearchArr();
        GetAgentsRadApprox(agents,x,y,z,rad,wrapX,wrapY,wrapZ);
        int ct=0;
        double radSq=rad*rad;
        for (int i = 0; i < agents.size(); i++) {
            Agent3DBase agent=(Agent3DBase)agents.get(i);
            if(DistSquared(x,y,z,agent.Xpt(),agent.Ypt(),agent.Zpt(),wrapX,wrapY,wrapZ)<radSq&&EvalAgent.EvalAgent((T)agent)){
                agents.set(ct,agents.get(i));
                ct++;
            }
        }
        T ret= agents.get(rn.Int(ct));
        usedAgentSearches.add(agents);
        return ret;
    }
    public T RandomAgentRect(int x,int y,int z,int width,int height,int depth,Rand rn,AgentToBool<T> EvalAgent){
        ArrayList<T> agents=GetFreshAgentSearchArr();
        GetAgentsRect(agents,x,y,z,width,height,depth);
        int ct=SubsetAgents(agents,EvalAgent);
        T ret= agents.get(rn.Int(ct));
        usedAgentSearches.add(agents);
        return ret;
    }

    public T RandomAgentHood(int[] hood,int x,int y,int z,Rand rn,AgentToBool<T> EvalAgent){
        ArrayList<T> agents=GetFreshAgentSearchArr();
        GetAgentsHood(agents,hood,x,y,z);
        int ct=SubsetAgents(agents,EvalAgent);
        T ret= agents.get(rn.Int(ct));
        usedAgentSearches.add(agents);
        return ret;
    }
    public T RandomAgentHoodMapped(int[]hood,int hoodLen,Rand rn){
        ArrayList<T> agents=GetFreshAgentSearchArr();
        GetAgentsHoodMapped(agents,hood,hoodLen);
        T ret= agents.get(rn.Int(agents.size()));
        usedAgentSearches.add(agents);
        return ret;
    }
    public T RandomAgentHoodMapped(int[]hood,int hoodLen,Rand rn,AgentToBool<T> EvalAgent){
        ArrayList<T> agents=GetFreshAgentSearchArr();
        GetAgentsHoodMapped(agents,hood,hoodLen);
        int ct=SubsetAgents(agents,EvalAgent);
        T ret= agents.get(rn.Int(agents.size()));
        usedAgentSearches.add(agents);
        return ret;
    }
    private class AgentsIterator3D implements Iterator<T>,Iterable<T>{
        final AgentGrid3D<T> myGrid;
        ArrayList<T>myAgents;
        int numAgents;
        int iCount;
        AgentsIterator3D(AgentGrid3D<T> grid){
            myGrid=grid;
        }
        public void Setup(ArrayList<T> myAgents){
            this.myAgents=myAgents;
            iCount=0;
            numAgents=myAgents.size();
        }

        @Override
        public boolean hasNext() {
            if(iCount== numAgents){
                myGrid.usedAgentSearches.add(myAgents);
                myGrid.usedIterIs.add(this);
                return false;
            }
            return true;
        }

        @Override
        public T next() {
            T ret=myAgents.get(iCount);
            iCount++;
            return ret;
        }

        @Override
        public Iterator<T> iterator() {
            return this;
        }
    }
}
