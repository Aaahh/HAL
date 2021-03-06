package LEARN_HERE.Tutorials.MultithreadExample;

import Framework.GridsAndAgents.AgentGrid2D;
import Framework.GridsAndAgents.AgentPT2D;
import Framework.GridsAndAgents.PDEGrid2D;
import Framework.Gui.GridWindow;
import Framework.Gui.OpenGL2DWindow;
import Framework.Rand;
import Framework.Tools.FileIO;
import Framework.Util;

class ExampleCell extends AgentPT2D<ExampleGrid> {
    int color;

    public void Init(){
        this.color=Util.RGB(G().rng.Double(),G().rng.Double(),G().rng.Double());
    }

    public void StepCell(double dieProb,double divProb){
        if(G().oxygen.Get(Isq())<0.7||G().rng.Double()<dieProb){
            //cell will die
            Dispose();
            return;
        }
        G().oxygen.Mul(Isq(),0.98);
        if(G().rng.Double()<divProb&&G().CountAt(Isq())<5){
            G().NewAgentPT(Xpt(),Ypt()).Init();
        }
        //cell will move
        G().rng.RandomPointInCircle(0.5,G().moveCoords);
        MoveSafePT(Xpt()+G().moveCoords[0],Ypt()+G().moveCoords[1]);
    }
}

public class ExampleGrid extends AgentGrid2D<ExampleCell> {
    Rand rng=new Rand();
    int[]divHood=Util.VonNeumannHood(false);
    double[]moveCoords=new double[2];
    PDEGrid2D oxygen;

    public ExampleGrid(int x, int y) {
        super(x, y, ExampleCell.class);
        oxygen =new PDEGrid2D(x,y);
        oxygen.SetAll(1);
    }
    public void Step(double dieProb, double divProb){
        for(ExampleCell cell:this){
            cell.StepCell(dieProb,divProb);
        }
        for (int i = 0; i < length; i++) {
            if(oxygen.Get(i)<1){
                oxygen.Add(i,0.02);
            }
        }
        oxygen.Diffusion(0.1);
    }

    public void DrawOxygen(GridWindow win){
        for (int i = 0; i < length; i++) {
            win.SetPix(i,Util.HeatMapRGB(oxygen.Get(i)));
        }
    }

    public void DrawModel(OpenGL2DWindow win){
        win.Clear(Util.BLACK);
        for (ExampleCell cell : this) {
            win.Circle(cell.Xpt(),cell.Ypt(),0.5,cell.color);
        }
        win.Show();
    }

    public static void main(String[]args){
        int x=100;
        int y=100;
        int timesteps=1000;
        double dieProb=0.1;
        double divProb=0.2;
        double[][]allOutput=new double[100][];

        Util.MultiThread(100,(int iThread)-> {
            double[]output=new double[20];

                    ExampleGrid model = new ExampleGrid(x, y);

                    //initialize model

                    for (int i = 0; i < timesteps; i++) {
                        if (model.GetPop() == 0) {
                            model.NewAgentPT(model.xDim / 2.0, model.yDim / 2.0).Init();
                        }
                        //model step
                        model.Step(dieProb, divProb);

                        //record output
                        if (i + 1 % 100 == 0) {
                            output[i / 100] = model.GetPop();
                            output[i / 100 + 1] = model.oxygen.GetAvg();
                        }
                    }
                    allOutput[iThread]=output;
                });
        //can you use iThread to vary the consumption rate?
        FileIO outFile=new FileIO("modelRun.csv","w");
        for (int i = 0; i < 100; i++) {
            outFile.WriteDelimit(allOutput[i],",");
            outFile.Write("\n");
        }
        outFile.Close();
    }
}

