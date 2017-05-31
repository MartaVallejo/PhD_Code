S=[]; %Satisfaction achieved
list = dir('../../HW/PhD/CollectedData/Scenarios/1Chapter/Scenario 6d/ICCS');
isub = [list(:).isdir]; %# returns logical vector
nameFolds = {list(isub).name}'; % return name folders in scenario

cd('../../HW/PhD/CollectedData/Scenarios/1Chapter/Scenario 6d/ICCS');
% Vectors one per each column of satisfaction files
for i=3:size(nameFolds,1)
    cd(char([nameFolds(i)]));
    % fullfile: Build full filename from parts
    gaSat=dir(fullfile(['SatisfactionGA' '*' '.txt']));
    ranSat=dir(fullfile(['SatisfactionRan' '*' '.txt']));
    cloSat=dir(fullfile(['SatisfactionClo' '*' '.txt']));
    j=1;
%     GA Satisfaction
    while (length(gaSat)>=j)
        if not(isempty(gaSat(j).name))
            fileID=fopen(gaSat(j).name);
            A = textscan(fileID,'%d %d %d %d %d %f %f %f %d %d %f %d %d %d %f %s %d');
            fclose(fileID);
            S=[S;  [6 1 str2num(nameFolds{i}) A{2}']];
        end;
        j=j+1;
    end;
%     Random Satisfaction
    j=1;
    while (length(ranSat)>=j)
        if not(isempty(ranSat(j).name))
            fileID=fopen(ranSat(j).name);
            A = textscan(fileID,'%d %d %d %d %d %f %f %f %d %d %f %d %d %d %f %s %d');
            fclose(fileID);
            S=[S; [6 0 0 A{2}']];
        end;
        j=j+1;
    end;
%     Closest Satisfaction
    j=1;
    while (length(cloSat)>=j)
        if not(isempty(cloSat(j).name))
            fileID=fopen(cloSat(j).name);
            A = textscan(fileID,'%d %d %d %d %d %f %f %f %d %d %f %d %d %d %f %s %d');
            fclose(fileID);
            S=[S; [6 2 0 A{2}']];
        end;
        j=j+1;
    end;   
    cd('..');
end;

list = dir('../../Scenario 9gc/ICCS');
isub = [list(:).isdir]; %# returns logical vector
nameFolds = {list(isub).name}'; % return name folders in scenario

cd('../../Scenario 9gc/ICCS');
% Vectors one per each column of satisfaction files
for i=3:size(nameFolds,1)
    cd(char([nameFolds(i)]));
    % fullfile: Build full filename from parts
    gaSat=dir(fullfile(['SatisfactionGA' '*' '.txt']));
    ranSat=dir(fullfile(['SatisfactionRan' '*' '.txt']));
    cloSat=dir(fullfile(['SatisfactionClo' '*' '.txt']));
    j=1;
%     GA Satisfaction
    while (length(gaSat)>=j)
        if not(isempty(gaSat(j).name))
            fileID=fopen(gaSat(j).name);
            A = textscan(fileID,'%d %d %d %d %d %f %f %f %d %d %f %d %d %d %f %s %d');
            fclose(fileID);
            S=[S;  [9 1 str2num(nameFolds{i}) A{2}']];
        end;
        j=j+1;
    end;
%     Random Satisfaction
    j=1;
    while (length(ranSat)>=j)
        if not(isempty(ranSat(j).name))
            fileID=fopen(ranSat(j).name);
            A = textscan(fileID,'%d %d %d %d %d %f %f %f %d %d %f %d %d %d %f %s %d');
            fclose(fileID);
            S=[S; [9 0 0 A{2}']];
        end;
        j=j+1;
    end;
%     Closest Satisfaction
    j=1;
    while (length(cloSat)>=j)
        if not(isempty(cloSat(j).name))
            fileID=fopen(cloSat(j).name);
            A = textscan(fileID,'%d %d %d %d %d %f %f %f %d %d %f %d %d %d %f %s %d');
            fclose(fileID);
            S=[S; [9 2 0 A{2}']];
        end;
        j=j+1;
    end;   
    cd('..');
end;

list = dir('../../Scenario 10b/ICCS');
isub = [list(:).isdir]; %# returns logical vector
nameFolds = {list(isub).name}'; % return name folders in scenario

cd('../../Scenario 10b/ICCS');
% Vectors one per each column of satisfaction files
for i=3:size(nameFolds,1)
    cd(char([nameFolds(i)]));
    % fullfile: Build full filename from parts
    gaSat=dir(fullfile(['SatisfactionGA' '*' '.txt']));
    ranSat=dir(fullfile(['SatisfactionRan' '*' '.txt']));
    cloSat=dir(fullfile(['SatisfactionClo' '*' '.txt']));
    j=1;
%     GA Satisfaction
    while (length(gaSat)>=j)
        if not(isempty(gaSat(j).name))
            fileID=fopen(gaSat(j).name);
            A = textscan(fileID,'%d %d %d %d %d %f %f %f %d %d %f %d %d %d %f %s %d');
            fclose(fileID);
            S=[S;  [10 1 str2num(nameFolds{i}) A{2}']];
        end;
        j=j+1;
    end;
%     Random Satisfaction
    j=1;
    while (length(ranSat)>=j)
        if not(isempty(ranSat(j).name))
            fileID=fopen(ranSat(j).name);
            A = textscan(fileID,'%d %d %d %d %d %f %f %f %d %d %f %d %d %d %f %s %d');
            fclose(fileID);
            S=[S; [10 0 0 A{2}']];
        end;
        j=j+1;
    end;
%     Closest Satisfaction
    j=1;
    while (length(cloSat)>=j)
        if not(isempty(cloSat(j).name))
            fileID=fopen(cloSat(j).name);
            A = textscan(fileID,'%d %d %d %d %d %f %f %f %d %d %f %d %d %d %f %s %d');
            fclose(fileID);
            S=[S; [10 2 0 A{2}']];
        end;
        j=j+1;
    end;   
    cd('..');
end;
pwd
cd('../../../../../../../MATLAB/General');
dlmwrite('satisfactionArea.txt', S);
