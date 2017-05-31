function Ring_GreenPricesPlot()
%Double Plot
% 1.- Plot average green price prices grouped by rings
%   In which tick of the clock?
% 2.- Average green prices in GA

cd('../General');
E = load('Rings.txt');

X = E(1,:);
steps = [1:1:size(X,2)];

subplot(2,1,1) % first subplot
hh=plot(steps, X)
set (hh, 'LineWidth', 2)
set (hh, 'Marker', '*')
hold on;
xlabel('Rings','fontweight','bold','fontsize',14)
ylabel('Avg Green Prices','fontweight','bold','fontsize',14)
title('Distribution of rural prices in one tick','fontweight','bold','fontsize',16)
hold off;

subplot(2,1,2) % second subplot
H = load('avgGreenPrice.txt');
I = grpstats(H, [H(:,1)]);
steps = [1:1:600];
h=plot(steps, I(2,2:end),'Color',[0,0.7,0.9])
set (h, 'LineWidth', 2)
hold on;
xlabel('time steps','fontweight','bold','fontsize',14)
ylabel('Avg Green Prices','fontweight','bold','fontsize',14)
title('Evolution of rural prices','fontweight','bold','fontsize',16)
handle=gcf;
saveas(handle, ['Ring_GreenPrices', 'eps'],'epsc')
%saveas(handle, ['Ring_GreenPrices', 'png'],'png')
hold off;
cd('../Functions');
end

