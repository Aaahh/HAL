package Framework.GridsAndAgents;

import Framework.Interfaces.AgentToBool;

import java.util.ArrayList;

import static Framework.Util.*;

/**
 * extend the AgentPT2D class if you want agents that exist on a 2D continuous lattice
 * with the possibility of stacking multiple agents on the same typeGrid square
 * @param <T> the extended AgentGrid2D class that the agents will live in
 * Created by rafael on 11/18/16.
 */
public class AgentPT2D<T extends AgentGrid2D> extends Agent2DBase<T>{
    private double ptX;
    private double ptY;
    AgentPT2D nextSq;
    AgentPT2D prevSq;
    @Override
    void Setup(double i){
        Setup((int)i);
    }
    @Override
    void Setup(double xPos,double yPos){
        this.ptX =xPos;
        this.ptY =yPos;
        iSq=this.myGrid.I(xPos,yPos);
        AddSQ(iSq);
    }

    @Override
    void Setup(double x, double y, double z) {
        throw new IllegalStateException("shouldn't be adding 2D agent to 3D typeGrid");
    }

    @Override
    void Setup(int i) {
        ptX =myGrid.ItoX(i)+0.5;
        ptY =myGrid.ItoY(i)+0.5;
        iSq =i;
        AddSQ(i);
    }

    @Override
    public void Dispose() {
        if(!alive){
            throw new RuntimeException("attepting to dispose already dead agent");
        }
        RemSQ();
        myGrid.agents.RemoveAgent(this);
        if(myNodes!=null){
            myNodes.DisposeAll();
        }
    }

    @Override
    void GetAllOnSquare(ArrayList<AgentBaseSpatial> putHere) {
        AgentPT2D toList=this;
        while (toList!=null){
            putHere.add(toList);
            toList=toList.nextSq;
        }
    }

    @Override
    int GetCountOnSquare() {
        return myGrid.counts[Isq()];
    }

    @Override
    int GetCountOnSquareEval(AgentToBool evalAgent) {
        int ct=0;
        AgentPT2D curr=this;
        while (curr!=null){
            if(evalAgent.EvalAgent(curr)){
                ct++;
                curr=curr.nextSq;
            }
        }
        return ct;
    }

    @Override
    void GetAllOnSquareEval(ArrayList<AgentBaseSpatial> putHere, AgentToBool evalAgent) {
        AgentPT2D toList=this;
        while (toList!=null){
            if(evalAgent.EvalAgent(toList)) {
                putHere.add(toList);
            }
            toList=toList.nextSq;
        }
    }

    void Setup(int xPos,int yPos){
        Setup(xPos+0.5,yPos+0.5);
    }

    @Override
    void Setup(int x, int y, int z) {
        throw new IllegalStateException("shouldn't be adding 2D agent to 3D typeGrid");
    }

    void AddSQ(int i){
        if(myGrid.grid[i]!=null){
            ((AgentPT2D)myGrid.grid[i]).prevSq=this;
            this.nextSq=(AgentPT2D)myGrid.grid[i];
        }
        myGrid.grid[i]=this;
        myGrid.counts[i]++;
    }
    void RemSQ(){
        if(myGrid.grid[iSq]==this){
            myGrid.grid[iSq]=this.nextSq;
        }
        if(nextSq!=null){
            nextSq.prevSq=prevSq;
        }
        if(prevSq!=null){
            prevSq.nextSq=nextSq;
        }
        prevSq=null;
        nextSq=null;
        myGrid.counts[iSq]--;
    }
    /**
     * Moves the agent to the center of the square at the specified coordinates
     */
    public void MoveSQ(int newX, int newY){
        if(!alive){
            throw new RuntimeException("Attempting to move dead agent");
        }
        RemSQ();
        iSq=myGrid.I(newX,newY);
        AddSQ(iSq);
        ptX =newX+0.5;
        ptY =newY+0.5;
    }

    public void MoveSQ(int i){
        if(!alive){
            throw new RuntimeException("attempting to move dead agent");
        }
        RemSQ();
        int x=G().ItoX(i);
        int y=G().ItoY(i);
        iSq=i;
        this.ptX=x+0.5;
        this.ptY=y+0.5;
        AddSQ(i);
    }


    /**
     * Moves the agent to the specified coordinates
     */
    public void MovePT(double newX, double newY){
        if(!alive){
            throw new RuntimeException("Attempting to move dead agent");
        }
        int xIntNew=(int)newX;
        int yIntNew=(int)newY;
        int xIntOld=(int) ptX;
        int yIntOld=(int) ptY;
        if(xIntNew!=xIntOld||yIntNew!=yIntOld) {
            RemSQ();
            iSq=myGrid.I(xIntNew,yIntNew);
            AddSQ(iSq);
        }
        ptX =newX;
        ptY =newY;
    }

    /**
     * Moves the agent to the specified coordinates, and either stops or wraps around at the edges
     */
    public void MoveSafePT(double newX, double newY, boolean wrapX, boolean wrapY){
        if(!alive){
            throw new RuntimeException("Attempting to move dead agent");
        }
        if (G().In(newX, newY)) {
            MovePT(newX, newY);
            return;
        }
        if (wrapX) {
            newX = ModWrap(newX, G().xDim);
        } else if (!InDim(G().xDim, newX)) {
            newX = Xpt();
        }
        if (wrapY) {
            newY = ModWrap(newY, G().yDim);
        } else if (!InDim(G().yDim, newY))
            newY = Ypt();
        MovePT(newX,newY);
    }
    public void MoveSafePT(double newX, double newY){
        if(!alive){
            throw new RuntimeException("Attempting to move dead agent");
        }
        if (G().In(newX, newY)) {
            MovePT(newX, newY);
            return;
        }
        if (G().wrapX) {
            newX = ModWrap(newX, G().xDim);
        } else if (!InDim(G().xDim, newX)) {
            newX = Xpt();
        }
        if (G().wrapY) {
            newY = ModWrap(newY, G().yDim);
        } else if (!InDim(G().yDim, newY))
            newY = Ypt();
        MovePT(newX,newY);
    }

    /**
     * gets the xDim coordinate of the agent
     */
    public double Xpt(){
        return ptX;
    }

    /**
     * gets the yDim coordinate of the agent
     */
    public double Ypt(){
        return ptY;
    }

    /**
     * gets the xDim coordinate of the square that the agent occupies
     */
    public int Xsq(){
        return (int) ptX;
    }

    /**
     * gets the yDim coordinate of the square that the agent occupies
     */
    public int Ysq(){
        return (int) ptY;
    }
    public<T extends AgentPT2D> double Xdisp(T other, boolean wrapX){
        return wrapX? DistWrap(other.Xpt(),Xpt(),G().xDim):Xpt()-other.Xpt();
    }
    public <T extends AgentPT2D> double Ydisp(T other, boolean wrapY){
        return wrapY? DistWrap(other.Ypt(),Ypt(),G().yDim):Ypt()-other.Ypt();
    }
}
