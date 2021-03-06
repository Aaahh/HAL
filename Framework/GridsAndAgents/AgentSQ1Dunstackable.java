package Framework.GridsAndAgents;

import Framework.Interfaces.AgentToBool;

import java.util.ArrayList;

import static Framework.Util.InDim;
import static Framework.Util.ModWrap;

/**
 * extend the AgentSQ2Dunstackable class if you want agents that exist on a 2D discrete lattice
 * without the possibility of stacking multiple agents on the same typeGrid square
 * @param <T> the extended Grid2unstackable class that the agents will live in
 * Created by rafael on 11/18/16.
 */

public class AgentSQ1Dunstackable<T extends AgentGrid1D> extends Agent1DBase<T>{

    public void SwapPosition(AgentBaseSpatial other){
        if(this.Isq()==other.Isq()){
            return;
        }
        if(!alive||!other.alive){
            throw new RuntimeException("attempting to move dead agent");
        }
        if(other.myGrid!=myGrid){
            throw new IllegalStateException("can't swap positions between agents on different grids!");
        }
        int iNew=other.Isq();
        int iNewOther=Isq();
        other.RemSQ();
        this.RemSQ();
        other.Setup(iNewOther);
        this.Setup(iNew);
    }
    void Setup(double i){
        Setup((int)i);
    }
    void Setup(double xSq,double ySq){
        throw new IllegalStateException("shouldn't be adding 1D agent to 2D typeGrid");
    }
    void Setup(double xSq,double ySq,double zSq){
        throw new IllegalStateException("shouldn't be adding 1D agent to 3D typeGrid");
    }

    @Override
    void Setup(int i) {
        iSq=i;
        AddSQ(i);
    }

    @Override
    void Setup(int x, int y) {
        throw new IllegalStateException("shouldn't be adding 1D agent to 2D typeGrid");
    }

    @Override
    void Setup(int x, int y, int z) {
        throw new IllegalStateException("shouldn't be adding 2D agent to 3D typeGrid");
    }

    /**
     * Moves the agent to the square with the specified index
     */
    public void MoveSQ(int x){
        //moves agent discretely
        if(!this.alive){
            throw new RuntimeException("Attempting to move dead agent!");
        }
        myGrid.grid[iSq]=null;
        iSq=x;
        AddSQ(x);
    }
    void AddSQ(int i){
        if(myGrid.grid[i]!=null){
            throw new RuntimeException("Adding multiple unstackable agents to the same square!");
        }
        myGrid.grid[i]=this;
    }
    void RemSQ(){
        myGrid.grid[iSq]=null;
    }


    public void MoveSafeSQ(int newX){
        if(!alive){
            throw new RuntimeException("Attempting to move dead agent");
        }
        if (G().In(newX)) {
            MoveSQ(newX);
            return;
        }
        if (G().wrapX) {
            newX = ModWrap(newX, G().xDim);
        } else if (!InDim(G().xDim, newX)) {
            newX = Xsq();
        }
        MoveSQ(newX);
    }
    public void MoveSafeSQ(int newX,boolean wrapX){
        if(!alive){
            throw new RuntimeException("Attempting to move dead agent");
        }
        if (G().In(newX)) {
            MoveSQ(newX);
            return;
        }
        if (wrapX) {
            newX = ModWrap(newX, G().xDim);
        } else if (!InDim(G().xDim, newX)) {
            newX = Xsq();
        }
        MoveSQ(newX);
    }
    /**
     * Gets the xDim coordinate of the square that the agent occupies
     */
    public int Xsq(){
        return iSq;
    }
    /**
     * Gets the xDim coordinate agent
     */
    public double Xpt(){
        return iSq+0.5;
    }
    /**
     * Deletes the agent
     */
    public void Dispose(){
        if(!this.alive){
            throw new RuntimeException("Attempting to dispose already dead agent!");
        }
        RemSQ();
        myGrid.agents.RemoveAgent(this);
        if(myNodes!=null){
            myNodes.DisposeAll();
        }
    }
    public void GetAllOnSquare(ArrayList<AgentBaseSpatial> putHere){
        putHere.add(this);
    }

    @Override
    void GetAllOnSquareEval(ArrayList<AgentBaseSpatial> putHere, AgentToBool evalAgent) {
        if(evalAgent.EvalAgent(this)) {
            putHere.add(this);
        }
    }

    @Override
    int GetCountOnSquare() {
        return 1;
    }

    @Override
    int GetCountOnSquareEval(AgentToBool evalAgent) {
        return evalAgent.EvalAgent(this)?1:0;
    }
    /**
     * Gets the index of the square that the agent occupies
     */
    public int Isq(){
        return iSq;
    }
}